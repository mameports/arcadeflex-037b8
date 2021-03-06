/******************************************************************************

  palette.c

  Palette handling functions.

  There are several levels of abstraction in the way MAME handles the palette,
  and several display modes which can be used by the drivers.

  Palette
  -------
  Note: in the following text, "color" refers to a color in the emulated
  game's virtual palette. For example, a game might be able to display 1024
  colors at the same time. If the game uses RAM to change the available
  colors, the term "palette" refers to the colors available at any given time,
  not to the whole range of colors which can be produced by the hardware. The
  latter is referred to as "color space".
  The term "pen" refers to one of the maximum MAX_PENS colors that can be
  used to generate the display. PC users might want to think of them as the
  colors available in VGA, but be warned: the mapping of MAME pens to the VGA
  registers is not 1:1, so MAME's pen 10 will not necessarily be mapped to
  VGA's color #10 (actually this is never the case). This is done to ensure
  portability, since on some systems it is not possible to do a 1:1 mapping.

  So, to summarize, the three layers of palette abstraction are:

  P1) The game virtual palette (the "colors")
  P2) MAME's MAX_PENS colors palette (the "pens")
  P3) The OS specific hardware color registers (the "OS specific pens")

  The array Machine.pens[] is a lookup table which maps game colors to OS
  specific pens (P1 to P3). When you are working on bitmaps at the pixel level,
  *always* use Machine.pens to map the color numbers. *Never* use constants.
  For example if you want to make pixel (x,y) of color 3, do:
  bitmap.line[y][x] = Machine.pens[3];
  Also remember that when using a dynamic palette (see below) Machine.pens[]
  is not constant, but changes as the games modifies its colors. Temporary
  bitmaps must be completely redrawn when palette_recalc() asks to do so.


  Lookup table
  ------------
  Palettes are only half of the story. To map the gfx data to colors, the
  graphics routines use a lookup table. For example if we have 4bpp tiles,
  which can have 256 different color codes, the lookup table for them will have
  256 * 2^4 = 4096 elements. For games using a palette RAM, the lookup table is
  usually a 1:1 map. For games using PROMs, the lookup table is often larger
  than the palette itself so for example the game can display 256 colors out
  of a palette of 16.

  The palette and the lookup table are initialized to default values by the
  main core, but can be initialized by the driver using the function
  MachineDriver.vh_init_palette(). For games using palette RAM, that
  function is usually not needed, and the lookup table can be set up by
  properly initializing the color_codes_start and total_color_codes fields in
  the GfxDecodeInfo array.
  When vh_init_palette() initializes the lookup table, it maps gfx codes
  to game colors (P1 above). The lookup table will be converted by the core to
  map to OS specific pens (P3 above), and stored in Machine.remapped_colortable.


  Display modes
  -------------
  The available display modes can be summarized in four categories:
  1) Static palette. Use this for games which use PROMs for color generation.
     The palette is initialized by vh_init_palette(), and never changed
     again.
  2) Dynamic palette. Use this for games which use RAM for color generation and
     have no more than MAX_PENS colors in the palette. The palette can be
     dynamically modified by the driver using the function
     palette_change_color(). MachineDriver.video_attributes must contain the
     flag VIDEO_MODIFIES_PALETTE.
     The function palette_recalc() must be called every frame *before* doing
     any rendering.
     The return code of palette_recalc() tells the driver whether the lookup
     table has changed, and therefore whether a screen refresh is needed.
  3) Dynamic shrinked palette. Use this for games which use RAM for color
     generation and have more than MAX_PENS colors in the palette.
     The difference with case 2) above is that the driver must do some
     additional work to allow for palette reduction without loss of quality.
     The palette_used_colors[] array can be changed to precisely indicate to
     the function which of the game colors are used. That way the palette
     system, so it can pick only the needed colors, and make the palette fit
     into MAX_PENS colors. Colors can also be marked as "transparent".
     palette_recalc() asks for a complete refresh only if the lookup table has
     changed for a color that was used both in the previous and in the current
     frame, therefore you must be careful to mark as used all colors stored in
     temporary bitmaps, even if you won't display them in the current frame: if
     you don't do that, the contents of the bitmap might become invalid without
     notice.
  4) 16-bit color. This should only be used for games which use more than
     MAX_PENS colors at a time. It is slower than the other modes, so it should
     be avoided whenever possible. Transparency support is limited.
     MachineDriver.video_attributes must contain VIDEO_MODIFIES_PALETTE, and
     GameDriver.flags must contain GAME_REQUIRES_16BIT.

  The dynamic shrinking of the palette works this way: as colors are requested,
  they are associated to a pen. When a color is no longer needed, the pen is
  freed and can be used for another color. When the code runs out of free pens,
  it compacts the palette, putting together colors with the same RGB
  components, then starts again to allocate pens for each new color. The bottom
  line of all this is that the pen assignment will automatically adapt to the
  game needs, and colors which change often will be assigned an exclusive pen,
  which can be modified using the video cards hardware registers without need
  for a screen refresh.
  The important difference between cases 3) and 4) is that in 3), color cycling
  (which many games use) is essentially free, while in 4) every palette change
  requires a screen refresh. The color quality in 3) is also better than in 4)
  if the game uses more than 5 bits per color component. For testing purposes,
  you can switch between the two modes by just adding/removing the
  GAME_REQUIRES_16BIT flag (but be warned about the limited transparency
  support in 16-bit mode).

******************************************************************************/

#ifndef PALETTE_H
#define PALETTE_H

#ifdef __cplusplus
extern "C" {
#endif

#define DYNAMIC_MAX_PENS 254	/* the Mac cannot handle more than 254 dynamic pens */
#define STATIC_MAX_PENS 256		/* but 256 static pens can be handled */


void palette_change_color(int color,UINT8 red,UINT8 green,UINT8 blue);

/* This array is used by palette_recalc() to know which colors are used, and which */
/* ones are transparent (see defines below). By default, it is initialized to */
/* PALETTE_COLOR_USED for all colors; this is enough in some cases. */
extern UINT8 *palette_used_colors;

void palette_increase_usage_count(int table_offset,unsigned int usage_mask,int color_flags);
void palette_decrease_usage_count(int table_offset,unsigned int usage_mask,int color_flags);
void palette_increase_usage_countx(int table_offset,int num_pens,const UINT8 *pen_data,int color_flags);
void palette_decrease_usage_countx(int table_offset,int num_pens,const UINT8 *pen_data,int color_flags);

/* If you want to dynamically change the usage array, call palette_init_used_colors() */
/* before setting used entries to PALETTE_COLOR_USED/PALETTE_COLOR_TRANSPARENT. */
/* The function automatically marks colors used by the TileMap system. */

const UINT8 *palette_recalc(void);

#define PALETTE_COLOR_UNUSED	0	/* This color is not needed for this frame */
#define PALETTE_COLOR_VISIBLE	1	/* This color is currently visible */
#define PALETTE_COLOR_CACHED	2	/* This color is cached in temporary bitmaps (but */
									/* not necessarily visible) */
	/* palette_recalc() will try to use always the same pens for the cached colors; */
	/* if it is forced to rearrange the pens, it will return TRUE to signal the */
	/* driver that it must refresh the cached bitmaps. */
#define PALETTE_COLOR_TRANSPARENT_FLAG	4	/* All colors using this attribute will be */
	/* mapped to the same pen, and no other colors will be mapped to that pen. */
	/* This way, transparencies can be handled by copybitmap(). */

/* backwards compatibility */
#define PALETTE_COLOR_USED			(PALETTE_COLOR_VISIBLE | PALETTE_COLOR_CACHED)
#define PALETTE_COLOR_TRANSPARENT	(PALETTE_COLOR_TRANSPARENT_FLAG | PALETTE_COLOR_USED)

/* if you use PALETTE_COLOR_TRANSPARENT, to do a transparency blit with copybitmap() */
/* pass it TRANSPARENCY_PEN, palette_transparent_pen. */
extern UINT16 palette_transparent_pen;


extern UINT16 *palette_shadow_table;



/* here are some functions to handle commonly used palette layouts, so you don't
   have to write your own paletteram_w() function. */

extern data8_t *paletteram;
extern data8_t *paletteram_2;	/* use when palette RAM is split in two parts */
extern data16_t *paletteram16;
extern data16_t *paletteram16_2;
extern data32_t *paletteram32;

READ16_HANDLER( paletteram16_word_r );
READ16_HANDLER( paletteram16_2_word_r );


/* _w       least significant byte first */
/* _swap_w  most significant byte first */
/* _split_w least and most significant bytes are not consecutive */
/* _word_w  use with 16 bit CPU */
/* R, G, B are bits, r, g, b are bytes */
/*                        MSB          LSB */
WRITE16_HANDLER( paletteram16_xxxxBBBBGGGGRRRR_word_w );
WRITE16_HANDLER( paletteram16_xxxxRRRRGGGGBBBB_word_w );
WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBxxxx_word_w );
WRITE16_HANDLER( paletteram16_BBBBGGGGRRRRxxxx_word_w );
WRITE16_HANDLER( paletteram16_xBBBBBGGGGGRRRRR_word_w );
WRITE16_HANDLER( paletteram16_xRRRRRGGGGGBBBBB_word_w );
WRITE16_HANDLER( paletteram16_xGGGGGRRRRRBBBBB_word_w );
WRITE16_HANDLER( paletteram16_RRRRRGGGGGBBBBBx_word_w );
WRITE16_HANDLER( paletteram16_IIIIRRRRGGGGBBBB_word_w );
WRITE16_HANDLER( paletteram16_RRRRGGGGBBBBIIII_word_w );
WRITE16_HANDLER( paletteram16_xrgb_word_w );



/* obsolete, will be removed */

#ifdef __cplusplus
}
#endif

#endif

/***************************************************************************

							  -= Cave Games =-

				driver by	Luca Elia (eliavit@unina.it)


Note:	if MAME_DEBUG is defined, pressing:

		X/C/V/B/Z  with  Q   shows layer 0 (tiles with priority 0/1/2/3/All)
		X/C/V/B/Z  with  W   shows layer 1 (tiles with priority 0/1/2/3/All)
		X/C/V/B/Z  with  E   shows layer 2 (tiles with priority 0/1/2/3/All)
		X/C/V/B/Z  with  A   shows sprites (tiles with priority 0/1/2/3/All)

		Keys can be used togheter!

		[ 1, 2 or 3 Scrolling Layers ]

		Layer Size:				512 x 512
		Tiles:					16x16x8 (16x16x4 in some games)

		[ 1024 Zooming Sprites ]

		There are 2 spriterams. A hardware register's bit selects
		the one to display (sprites double buffering).

		The sprites are NOT tile based: the "tile" size and start
		address is selectable for each sprite with a 16 pixel granularity.


**************************************************************************/
/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class cave
{
	
	
	/* Variables that driver has access to: */
	
	data16_t *cave_videoregs;
	
	data16_t *cave_vram_0, *cave_vctrl_0;
	data16_t *cave_vram_1, *cave_vctrl_1;
	data16_t *cave_vram_2, *cave_vctrl_2;
	
	/* Variables only used here: */
	
	static struct sprite_list *sprite_list;
	static struct tilemap *tilemap_0, *tilemap_1, *tilemap_2;
	
	/* Variables defined in driver: */
	
	extern int cave_spritetype;
	
	
	/***************************************************************************
	
							Callbacks for the TileMap code
	
								  [ Tiles Format ]
	
	Offset:
	
	0.w			fe-- ---- ---- ---		Priority
				--dc ba98 ---- ----		Color
				---- ---- 7654 3210
	
	2.w									Code
	
	
	
	***************************************************************************/
	
	#define DIM_NX		(0x20)
	#define DIM_NY		(0x20)
	
	
	#define CAVE_TILEMAP(_n_) \
	public static GetTileInfoPtr get_tile_info_##_n_ = new GetTileInfoPtr() { public void handler(int tile_index)  \
	{ \
		UINT32 code		=	(cave_vram_##_n_[ tile_index * 2 + 0] << 16 )+ \
							 cave_vram_##_n_[ tile_index * 2 + 1]; \
		SET_TILE_INFO( _n_ ,  code & 0x00ffffff , (code & 0x3f000000) >> (32-8) ); \
		tile_info.priority = (code & 0xc0000000)>> (32-2); \
	} }; \
	\
	WRITE16_HANDLER( cave_vram_##_n_##_w ) \
	{ \
		COMBINE_DATA(&cave_vram_##_n_[offset]); \
		if ( (offset/2) < DIM_NX * DIM_NY ) \
			tilemap_mark_tile_dirty(tilemap_##_n_, offset/2 ); \
	} \
	\
	WRITE16_HANDLER( cave_vram_##_n_##_8x8_w ) \
	{ \
		offset %= ( (DIM_NX * 2) * (DIM_NY * 2) * 2); /* mirrored RAM */ \
		COMBINE_DATA(&cave_vram_##_n_[offset]); \
		tilemap_mark_tile_dirty(tilemap_##_n_, offset/2 ); \
	}
	
	CAVE_TILEMAP(0)
	CAVE_TILEMAP(1)
	CAVE_TILEMAP(2)
	
	
	
	
	
	
	/***************************************************************************
	
									Vh_Start
	
	***************************************************************************/
	
	
	/* 3 Layers (layer 3 is made of 8x8 tiles!) */
	public static VhStartPtr ddonpach_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0 = tilemap_create(	get_tile_info_0,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_1 = tilemap_create(	get_tile_info_1,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		/* 8x8 tiles here! */
		tilemap_2 = tilemap_create(	get_tile_info_2,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									8,8,
									DIM_NX*2,DIM_NY*2 );
	
	
		sprite_list = sprite_list_create(spriteram_size[0] / 0x10 / 2, SPRITE_LIST_BACK_TO_FRONT | SPRITE_LIST_RAW_DATA );
	
		if (tilemap_0 && tilemap_1 && tilemap_2 && sprite_list)
		{
			tilemap_set_scroll_rows(tilemap_0,1);
			tilemap_set_scroll_cols(tilemap_0,1);
			tilemap_set_transparent_pen(tilemap_0,0);
	
			tilemap_set_scroll_rows(tilemap_1,1);
			tilemap_set_scroll_cols(tilemap_1,1);
			tilemap_set_transparent_pen(tilemap_1,0);
	
			tilemap_set_scroll_rows(tilemap_2,1);
			tilemap_set_scroll_cols(tilemap_2,1);
			tilemap_set_transparent_pen(tilemap_2,0);
	
			tilemap_set_scrolldx( tilemap_0, -0x6c, -0x57 );
			tilemap_set_scrolldx( tilemap_1, -0x6d, -0x56 );
	//		tilemap_set_scrolldx( tilemap_2, -0x6e, -0x55 );
			tilemap_set_scrolldx( tilemap_2, -0x6e -7, -0x55 +7-1 );
	
			tilemap_set_scrolldy( tilemap_0, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_1, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_2, -0x11, -0x100 );
	
			sprite_list.max_priority = 3;
			sprite_list.sprite_type = SPRITE_TYPE_ZOOM;
	
			return 0;
		}
		else return 1;
	} };
	
	/* 3 Layers (like esprade but with different scroll delta's) */
	public static VhStartPtr guwange_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0 = tilemap_create(	get_tile_info_0,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_1 = tilemap_create(	get_tile_info_1,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_2 = tilemap_create(	get_tile_info_2,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
	
		sprite_list = sprite_list_create(spriteram_size[0] / 0x10 / 2, SPRITE_LIST_BACK_TO_FRONT | SPRITE_LIST_RAW_DATA );
	
		if (tilemap_0 && tilemap_1 && tilemap_2 && sprite_list)
		{
			tilemap_set_scroll_rows(tilemap_0,1);
			tilemap_set_scroll_cols(tilemap_0,1);
			tilemap_set_transparent_pen(tilemap_0,0);
	
			tilemap_set_scroll_rows(tilemap_1,1);
			tilemap_set_scroll_cols(tilemap_1,1);
			tilemap_set_transparent_pen(tilemap_1,0);
	
			tilemap_set_scroll_rows(tilemap_2,1);
			tilemap_set_scroll_cols(tilemap_2,1);
			tilemap_set_transparent_pen(tilemap_2,0);
	
	//		tilemap_set_scrolldx( tilemap_0, -0x6c, -0x57 );
	//		tilemap_set_scrolldx( tilemap_1, -0x6d, -0x56 );
	//		tilemap_set_scrolldx( tilemap_2, -0x6e, -0x55 );
	tilemap_set_scrolldx( tilemap_0, -0x6c +2, -0x57 -2 );
	tilemap_set_scrolldx( tilemap_1, -0x6d +2, -0x56 -2 );
	tilemap_set_scrolldx( tilemap_2, -0x6e +2, -0x55 -2 );
	
			tilemap_set_scrolldy( tilemap_0, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_1, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_2, -0x11, -0x100 );
	//tilemap_set_scrolldy( tilemap_2, -0x11 +8, -0x100 -8 );
	
			sprite_list.max_priority = 3;
			sprite_list.sprite_type = SPRITE_TYPE_ZOOM;
	
			return 0;
		}
		else return 1;
	} };
	
	
	
	/* 3 Layers */
	public static VhStartPtr esprade_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0 = tilemap_create(	get_tile_info_0,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_1 = tilemap_create(	get_tile_info_1,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_2 = tilemap_create(	get_tile_info_2,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
	
		sprite_list = sprite_list_create(spriteram_size[0] / 0x10 / 2, SPRITE_LIST_BACK_TO_FRONT | SPRITE_LIST_RAW_DATA );
	
		if (tilemap_0 && tilemap_1 && tilemap_2 && sprite_list)
		{
			tilemap_set_scroll_rows(tilemap_0,1);
			tilemap_set_scroll_cols(tilemap_0,1);
			tilemap_set_transparent_pen(tilemap_0,0);
	
			tilemap_set_scroll_rows(tilemap_1,1);
			tilemap_set_scroll_cols(tilemap_1,1);
			tilemap_set_transparent_pen(tilemap_1,0);
	
			tilemap_set_scroll_rows(tilemap_2,1);
			tilemap_set_scroll_cols(tilemap_2,1);
			tilemap_set_transparent_pen(tilemap_2,0);
	
			tilemap_set_scrolldx( tilemap_0, -0x6c, -0x57 );
			tilemap_set_scrolldx( tilemap_1, -0x6d, -0x56 );
			tilemap_set_scrolldx( tilemap_2, -0x6e, -0x55 );
	
			tilemap_set_scrolldy( tilemap_0, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_1, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_2, -0x11, -0x100 );
	
			sprite_list.max_priority = 3;
			sprite_list.sprite_type = SPRITE_TYPE_ZOOM;
	
			return 0;
		}
		else return 1;
	} };
	
	
	
	/* 2 Layers */
	public static VhStartPtr dfeveron_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0 = tilemap_create(	get_tile_info_0,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_1 = tilemap_create(	get_tile_info_1,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_2 = 0;
	
		sprite_list = sprite_list_create(spriteram_size[0] / 0x10 / 2, SPRITE_LIST_BACK_TO_FRONT | SPRITE_LIST_RAW_DATA );
	
		if (tilemap_0 && tilemap_1 && sprite_list)
		{
			tilemap_set_scroll_rows(tilemap_0,1);
			tilemap_set_scroll_cols(tilemap_0,1);
			tilemap_set_transparent_pen(tilemap_0,0);
	
			tilemap_set_scroll_rows(tilemap_1,1);
			tilemap_set_scroll_cols(tilemap_1,1);
			tilemap_set_transparent_pen(tilemap_1,0);
	
	/*
		Scroll registers (on dfeveron logo screen):
			8195	a1f7 (both)	=	200-6b	200-9	(flip off)
			01ac	2108 (both)	=	200-54	100+8	(flip on)
		Video registers:
			0183	0001		=	200-7d	001		(flip off)
			81bf	80f0		=	200-41	100-10	(flip on)
	*/
	
			tilemap_set_scrolldx( tilemap_0, -0x6c, -0x54 );
			tilemap_set_scrolldx( tilemap_1, -0x6d, -0x53 );
	
			tilemap_set_scrolldy( tilemap_0, -0x11, -0x100 );
			tilemap_set_scrolldy( tilemap_1, -0x11, -0x100 );
	
			sprite_list.max_priority = 3;
			sprite_list.sprite_type = SPRITE_TYPE_ZOOM;
	
			return 0;
		}
		else return 1;
	} };
	
	
	
	/* 1 Layer */
	public static VhStartPtr uopoko_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0 = tilemap_create(	get_tile_info_0,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									16,16,
									DIM_NX,DIM_NY );
	
		tilemap_1 = 0;
	
		tilemap_2 = 0;
	
		sprite_list = sprite_list_create(spriteram_size[0] / 0x10 / 2, SPRITE_LIST_BACK_TO_FRONT | SPRITE_LIST_RAW_DATA );
	
		if (tilemap_0 && sprite_list)
		{
			tilemap_set_scroll_rows(tilemap_0,1);
			tilemap_set_scroll_cols(tilemap_0,1);
			tilemap_set_transparent_pen(tilemap_0,0);
	
			tilemap_set_scrolldx( tilemap_0, -0x6d, -0x54 );
	
			tilemap_set_scrolldy( tilemap_0, -0x11, -0x100 );
	
			sprite_list.max_priority = 3;
			sprite_list.sprite_type = SPRITE_TYPE_ZOOM;
	
			return 0;
		}
		else return 1;
	} };
	
	
	
	/***************************************************************************
	
								Vh_Init_Palette
	
	***************************************************************************/
	
	/* Function needed for games with 4 bit sprites, rather than 8 bit */
	
	
	public static VhConvertColorPromPtr dfeveron_vh_init_palette = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int color, pen;
	
		/* Fill the 0-3fff range, used by sprites ($40 color codes * $100 pens)
		   Here sprites have 16 pens, but the sprite drawing routine always
		   multiplies the color code by $100 (for consistency).
		   That's why we need this function.	*/
	
		for( color = 0; color < 0x40; color++ )
			for( pen = 0; pen < 16; pen++ )
				colortable[color * 256 + pen] = color * 16 + pen;
	} };
	
	
	
	public static VhConvertColorPromPtr ddonpach_vh_init_palette = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int color, pen;
	
		/* Fill the 8000-83ff range ($40 color codes * $10 pens) for
		   layers 1 & 2 which are 4 bits deep rather than 8 bits deep
		   like layer 3, but use the first 16 color of every 256 for
		   any given color code. */
	
		for( color = 0; color < 0x40; color++ )
			for( pen = 0; pen < 16; pen++ )
				colortable[color * 16 + pen + 0x8000] = 0x4000 + color * 256 + pen;
	} };
	
	
	
	
	
	/***************************************************************************
	
								Sprites Drawing
	
	***************************************************************************/
	
	
	/* --------------------------[ Sprites Format ]----------------------------
	
	Offset:		Format:					Value:
	
	00.w		fedc ba98 76-- ----		X Position
				---- ---- --54 3210
	
	02.w		fedc ba98 76-- ----		Y Position
				---- ---- --54 3210
	
	04.w		fe-- ---- ---- ----
				--dc ba98 ---- ----		Color
				---- ---- 76-- ----
				---- ---- --54 ----		Priority
				---- ---- ---- 3---		Flip X
				---- ---- ---- -2--		Flip Y
				---- ---- ---- --10		Code High Bit(s?)
	
	06.w								Code Low Bits
	
	08/0A.w								Zoom X / Y
	
	0C.w		fedc ba98 ---- ----		Tile Size X
				---- ---- 7654 3210		Tile Size Y
	
	0E.w								Unused
	
	------------------------------------------------------------------------ */
	
	static void get_sprite_info(void)
	{
		const int region				=	REGION_GFX4;
	
		const unsigned short *base_pal	=	Machine.remapped_colortable + 0;
		const UBytePtr base_gfx	=	memory_region(region);
		const UBytePtr gfx_max	=	base_gfx + memory_region_length(region);
	
		int sprite_bank					=	cave_videoregs[ 4 ] & 1;
	
		data16_t      *source			=	spriteram16 + ((spriteram_size/2) / 2) * sprite_bank;
		struct sprite *sprite			=	sprite_list.sprite;
		const struct sprite *finish		=	sprite + spriteram_size / 0x10 / 2;
	
		int	glob_flipx	=	cave_videoregs[ 0 ] & 0x8000;
		int	glob_flipy	=	cave_videoregs[ 1 ] & 0x8000;
	
		int max_x		=	Machine.drv.screen_width;
		int max_y		=	Machine.drv.screen_height;
	
		for (; sprite < finish; sprite++,source+=8 )
		{
			int x,y,attr,code,zoomx,zoomy,size,flipx,flipy;
			if ( cave_spritetype == 0)	// most of the games
			{
				x			=		source[ 0 ];
				y			=		source[ 1 ];
				attr		=		source[ 2 ];
				code		=		source[ 3 ];
				zoomx		=		source[ 4 ];
				zoomy		=		source[ 5 ];
				size		=		source[ 6 ];
			}
			else						// ddonpach
			{
				attr		=		source[ 0 ];
				code		=		source[ 1 ];
				x			=		source[ 2 ] << 6;
				y			=		source[ 3 ] << 6;
				size		=		source[ 4 ];
				// no zooming
				zoomx		=		0x100;
				zoomy		=		0x100;
			}
	
			code		+=		(attr & 3) << 16;
	
			flipx		=		attr & 0x0008;
			flipy		=		attr & 0x0004;
	
			if ((x & 0x8000) != 0)	x -= 0x10000;
			if ((y & 0x8000) != 0)	y -= 0x10000;
	
			x /= 0x40;		y /= 0x40;
	
			sprite.priority		=	(attr & 0x0030) >> 4;
			sprite.flags			=	SPRITE_VISIBLE;
	
			sprite.tile_width		=	( (size >> 8) & 0x1f ) * 16;
			sprite.tile_height		=	( (size >> 0) & 0x1f ) * 16;
	
			sprite.total_width		=	(sprite.tile_width  * zoomx) / 0x100;
			sprite.total_height	=	(sprite.tile_height * zoomy) / 0x100;
	
			sprite.pen_data		=	base_gfx + (16*16) * code;
			sprite.line_offset		=	sprite.tile_width;
	
			sprite.pal_data		=	base_pal + (attr & 0x3f00);	// first 0x4000 colors
	
			/* Bound checking */
			if ((sprite.pen_data + sprite.tile_width * sprite.tile_height - 1) >= gfx_max )
				{sprite.flags = 0;	continue;}
	
			if (glob_flipx != 0)	{ x = max_x - x - sprite.total_width;	flipx = !flipx; }
			if (glob_flipy != 0)	{ y = max_y - y - sprite.total_height;	flipy = !flipy; }
	
			sprite.x				=	x;
			sprite.y				=	y;
	
			if (flipx != 0)	sprite.flags |= SPRITE_FLIPX;
			if (flipy != 0)	sprite.flags |= SPRITE_FLIPY;
		}
	}
	
	
	
	/***************************************************************************
	
									Screen Drawing
	
	***************************************************************************/
	
	public static VhUpdatePtr cave_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int pri;
		int layers_ctrl = -1;
	
		int	glob_flipx	=	cave_videoregs[ 0 ] & 0x8000;
		int	glob_flipy	=	cave_videoregs[ 1 ] & 0x8000;
	
		tilemap_set_flip(ALL_TILEMAPS, (glob_flipx ? TILEMAP_FLIPX : 0) | (glob_flipy ? TILEMAP_FLIPY : 0) );
	
		tilemap_set_enable( tilemap_0,    cave_vctrl_0[2] & 1 );
		tilemap_set_scrollx(tilemap_0, 0, cave_vctrl_0[0] );
		tilemap_set_scrolly(tilemap_0, 0, cave_vctrl_0[1] );
	
		if (tilemap_1 != 0)
		{
			tilemap_set_enable( tilemap_1,    cave_vctrl_1[2] & 1 );
			tilemap_set_scrollx(tilemap_1, 0, cave_vctrl_1[0] );
			tilemap_set_scrolly(tilemap_1, 0, cave_vctrl_1[1] );
		}
	
		if (tilemap_2 != 0)
		{
			tilemap_set_enable( tilemap_2,    cave_vctrl_2[2] & 1 );
			tilemap_set_scrollx(tilemap_2, 0, cave_vctrl_2[0] );
			tilemap_set_scrolly(tilemap_2, 0, cave_vctrl_2[1] );
		}
	
	
	#ifdef MAME_DEBUG
	if ( keyboard_pressed(KEYCODE_Z) || keyboard_pressed(KEYCODE_X) || keyboard_pressed(KEYCODE_C) ||
	     keyboard_pressed(KEYCODE_V) || keyboard_pressed(KEYCODE_B) )
	{
		int msk = 0, val = 0;
	
		if (keyboard_pressed(KEYCODE_X))	val = 1;	// priority 0 only
		if (keyboard_pressed(KEYCODE_C))	val = 2;	// ""       1
		if (keyboard_pressed(KEYCODE_V))	val = 4;	// ""       2
		if (keyboard_pressed(KEYCODE_B))	val = 8;	// ""       3
	
		if (keyboard_pressed(KEYCODE_Z))	val = 1|2|4|8;	// All of the above priorities
	
		if (keyboard_pressed(KEYCODE_Q))	msk |= val << 0;	// for layer 0
		if (keyboard_pressed(KEYCODE_W))	msk |= val << 4;	// for layer 1
		if (keyboard_pressed(KEYCODE_E))	msk |= val << 8;	// for layer 2
		if (keyboard_pressed(KEYCODE_A))	msk |= val << 12;	// for sprites
		if (msk != 0) layers_ctrl &= msk;
	
	#if 1
		{
			char buf[80];
			sprintf(buf,"%04X %04X %04X %04X %04X %04X %04X %04X",
					cave_videoregs[0], cave_videoregs[1],
					cave_videoregs[2], cave_videoregs[3],
					cave_videoregs[4], cave_videoregs[5],
					cave_videoregs[6], cave_videoregs[7] );
			usrintf_showmessage(buf);
		}
	#endif
	
	}
	#endif
	
		tilemap_update(ALL_TILEMAPS);
	
		palette_init_used_colors();
	
		get_sprite_info();
	
		sprite_update();
	
		palette_recalc();
	
		/* Clear the background if at least one of layer 0's tile priorities
		   is lacking */
	
		if ((layers_ctrl & 0xf) != 0xf)
			fillbitmap(bitmap,palette_transparent_pen,&Machine.visible_area);
	
		/* Pen 0 of layer 0's tiles (any priority) goes below anything else */
	
		for ( pri = 0; pri < 4; pri++ )
			if ((layers_ctrl&(1<<(pri+0)))&&tilemap_0)	tilemap_draw(bitmap, tilemap_0, TILEMAP_IGNORE_TRANSPARENCY | pri,0);
	
		/* Draw the rest with transparency */
	
		for ( pri = 0; pri < 4; pri++ )
		{
			if ((layers_ctrl&(1<<(pri+12))))			sprite_draw(sprite_list, pri);
			if ((layers_ctrl&(1<<(pri+0)))&&tilemap_0)	tilemap_draw(bitmap, tilemap_0, pri,0);
			if ((layers_ctrl&(1<<(pri+4)))&&tilemap_1)	tilemap_draw(bitmap, tilemap_1, pri,0);
			if ((layers_ctrl&(1<<(pri+8)))&&tilemap_2)	tilemap_draw(bitmap, tilemap_2, pri,0);
		}
	} };
}

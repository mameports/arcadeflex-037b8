/***************************************************************************

							-= Psikyo Games =-

				driver by	Luca Elia (eliavit@unina.it)


							[ 2 Scrolling Layers ]


		Layer Size:				512 x 2048 ($20 x $80 tiles)
		Tiles:					16x16x4
		Color Codes:			8


						[ ~ $300 Multi-Sized Sprites ]


		Each sprite is made of 16x16 tiles, up to 8x8 tiles.

		There are $300 sprites, followed by a list of the indexes
		of the sprites to actually display ($400 max). The list is
		terminated by the special index value FFFF.

		The tile code specified for a sprite is actually fed to a
		ROM holding a look-up table with the real tile code to display.


**************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class psikyo
{
	
	
	/* Variables that driver has access to: */
	
	data16_t *psikyo_vram_0, *psikyo_vram_1, *psikyo_vregs;
	
	
	/* Variables only used here: */
	
	static struct tilemap *tilemap_0, *tilemap_1;
	static int flipscreen;
	
	
	/***************************************************************************
	
							Callbacks for the TileMap code
	
								  [ Tiles Format ]
	
	Offset:
	
	0000.w			fed- ---- ---- ----		Color
					---c ba98 7654 3210		Code
	
	***************************************************************************/
	
	#define NX			(0x20)
	#define NY			(0x80)
	
	public static GetTileInfoPtr get_tile_info_0 = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		data16_t code = psikyo_vram_0[tile_index];
		SET_TILE_INFO(1, (code & 0x1fff), (code >> 13) & 7 );
	} };
	public static GetTileInfoPtr get_tile_info_1 = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		data16_t code = psikyo_vram_1[tile_index];
		SET_TILE_INFO(2, (code & 0x1fff), (code >> 13) & 7 );
	} };
	
	
	WRITE16_HANDLER( psikyo_vram_0_w )
	{
		COMBINE_DATA(&psikyo_vram_0[offset]);
		tilemap_mark_tile_dirty(tilemap_0, offset);
	}
	
	WRITE16_HANDLER( psikyo_vram_1_w )
	{
		COMBINE_DATA(&psikyo_vram_1[offset]);
		tilemap_mark_tile_dirty(tilemap_1, offset);
	}
	
	
	
	
	
	public static VhStartPtr psikyo_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0	=	tilemap_create(	get_tile_info_0,
										tilemap_scan_rows,
										TILEMAP_TRANSPARENT,
										16,16,
										NX,NY );
	
		tilemap_1	=	tilemap_create(	get_tile_info_1,
										tilemap_scan_rows,
										TILEMAP_TRANSPARENT,
										16,16,
										NX,NY );
	
		if (tilemap_0 && tilemap_1)
		{
			tilemap_set_scroll_rows(tilemap_0,1);
			tilemap_set_scroll_cols(tilemap_0,1);
			tilemap_set_transparent_pen(tilemap_0,15);
	
			tilemap_set_scroll_rows(tilemap_1,1);
			tilemap_set_scroll_cols(tilemap_1,1);
			tilemap_set_transparent_pen(tilemap_1,15);
			return 0;
		}
		else return 1;
	} };
	
	
	
	/***************************************************************************
	
									Sprites Drawing
	
	Offset:			Value:
	
	0000/2.w		Y/X + Y/X Size
	
						fedc ---- ---- ----		-
						---- ba9- ---- ----		Tiles along Y/X
						---- ---8 7654 3210		Position
	
	
	0004.w			Color + Flags
	
						f--- ---- ---- ----		Flip Y
						-e-- ---- ---- ----		Flip X
						--d- ---- ---- ----		-
						---c ba98 ---- ----		Color
						---- ---- 76-- ----		Priority
						---- ---- --54 321-		-
						---- ---- ---- ---0		Code High Bit
	
	
	0006.w										Code Low Bits
	
					(Code goes into a LUT in ROM where
					 the real tile code is.)
	
	
	Note:	Not all sprites are displayed: in the top part of spriteram
			(e.g. 401800-401fff) there's the list of sprites indexes to
			actually display, terminated by FFFF.
	
			The last entry (e.g. 401ffe) is special and holds some flags.
	
	
	***************************************************************************/
	
	static void psikyo_draw_sprites(struct osd_bitmap *bitmap/*,int priority*/)
	{
		int offs;
	
		UBytePtr TILES	=	memory_region(REGION_USER1);	// Sprites LUT
		int TILES_LEN			=	memory_region_length(REGION_USER1);
	
		int width	=	Machine.drv.screen_width;
		int height	=	Machine.drv.screen_height;
	
		/* Exit if sprites are disabled */
		if ( spriteram16_2[ (0x800-2)/2 ] & 1 )	return;
	
		/* Look for "end of sprites" marker in the sprites list */
		for ( offs = 0/2 ; offs < (0x800-2)/2 ; offs += 2/2 )	// skip last "sprite"
		{
			data16_t sprite = spriteram16_2[ offs ];
			if (sprite == 0xffff)	break;
		}
		offs -= 2/2;
	
		for ( ; offs >= 0/2 ; offs -= 2/2 )
		{
			data16_t *source;
			int	sprite;
	
			int	x,y,attr,code,flipx,flipy,nx,ny;
	
			int xstart, ystart, xend, yend;
			int xinc, yinc, dx, dy;
	
			/* Get next entry in the list */
			sprite	=	spriteram16_2[ offs ];
	
			sprite	%=	0x300;
			source	=	&spriteram16[ sprite*8/2 ];
	
			/* Draw this sprite */
	
			y		=	source[ 0/2 ];
			x		=	source[ 2/2 ];
			attr	=	source[ 4/2 ];
			code	=	source[ 6/2 ] + ((attr & 1) << 16);
	
			flipx	=	attr & 0x4000;
			flipy	=	attr & 0x8000;
	
			nx	=	((x >> 9) & 0x7) + 1;
			ny	=	((y >> 9) & 0x7) + 1;
	
			x = (x & 0x1ff);
			y = (y & 0x0ff) - (y & 0x100);
	
			/* 180-1ff are negative coordinates. Note that $80 pixels is
			   the maximum extent of a sprite, which can therefore be moved
			   out of screen without problems */
			if (x >= 0x180)	x -= 0x200;
	
			if (flipscreen != 0)
			{
				x = width  - x - (nx-1)*16;
				y = height - y - (ny-1)*16;
				flipx = !flipx;
				flipy = !flipy;
			}
	
			if (flipx != 0)	{ xstart = nx-1;  xend = -1;  xinc = -1; }
			else		{ xstart = 0;     xend = nx;  xinc = +1; }
	
			if (flipy != 0)	{ ystart = ny-1;  yend = -1;   yinc = -1; }
			else		{ ystart = 0;     yend = ny;   yinc = +1; }
	
			for (dy = ystart; dy != yend; dy += yinc)
			{
				for (dx = xstart; dx != xend; dx += xinc)
				{
					int addr	=	(code*2) & (TILES_LEN-1);
	
					pdrawgfx(bitmap,Machine.gfx[0],
							TILES[addr+1] * 256 + TILES[addr],
							attr >> 8,
							flipx, flipy,
							x + dx * 16, y + dy * 16,
							&Machine.visible_area,TRANSPARENCY_PEN,15	,
							(attr & 0xc0) ? 2 : 0);	// layer 0&1 have pri 0&1
	
					code++;
				}
			}
		}
	}
	
	
	static void psikyo_mark_sprite_colors(void)
	{
		int count = 0;
		int offs,i,col,colmask[0x100];
	
		UBytePtr TILES	=	memory_region(REGION_USER1);	// Sprites LUT
		int TILES_LEN			=	memory_region_length(REGION_USER1);
	
		unsigned int *pen_usage	=	Machine.gfx[0].pen_usage;
		int total_elements		=	Machine.gfx[0].total_elements;
		int color_codes_start	=	Machine.drv.gfxdecodeinfo[0].color_codes_start;
		int total_color_codes	=	Machine.drv.gfxdecodeinfo[0].total_color_codes;
	
		int xmin = Machine.visible_area.min_x;
		int xmax = Machine.visible_area.max_x;
		int ymin = Machine.visible_area.min_y;
		int ymax = Machine.visible_area.max_y;
	
		/* Exit if sprites are disabled */
		if ( spriteram16_2[ (0x800-2)/2 ] & 1 )	return;
	
		memset(colmask, 0, sizeof(colmask));
	
		for ( offs = 0/2; offs < (0x800-2)/2 ; offs += 2/2 )
		{
			data16_t *source;
			int	sprite;
	
			int	x,y,attr,code,flipx,flipy,nx,ny;
	
			int xstart, ystart, xend, yend;
			int xinc, yinc, dx, dy;
			int color;
	
			/* Get next entry in the list */
			sprite	=	spriteram16_2[ offs ];
	
			/* End of sprites list */
			if (sprite == 0xffff)	break;
	
			sprite	%=	0x300;
			source	=	&spriteram16[ sprite*8/2 ];
	
			/* Mark the pens used by the visible portion of this sprite */
	
			y		=	source[ 0/2 ];
			x		=	source[ 2/2 ];
			attr	=	source[ 4/2 ];
			code	=	source[ 6/2 ] + ((attr & 1) << 16);
	
			flipx	=	attr & 0x4000;
			flipy	=	attr & 0x8000;
	
			color	=	(attr >> 8) % total_color_codes;
	
			nx	=	((x >> 9) & 0x7) + 1;
			ny	=	((y >> 9) & 0x7) + 1;
	
			x = (x & 0x1ff);
			y = (y & 0x0ff) - (y & 0x100);
	
			if (x >= 0x180)	x -= 0x200;
	
			/* No need to account for screen flipping, but we have
			   to consider sprite flipping though: */
	
			if (flipx != 0)	{ xstart = nx-1;  xend = -1;  xinc = -1; }
			else		{ xstart = 0;     xend = nx;  xinc = +1; }
	
			if (flipy != 0)	{ ystart = ny-1;  yend = -1;   yinc = -1; }
			else		{ ystart = 0;     yend = ny;   yinc = +1; }
	
			for (dy = ystart; dy != yend; dy += yinc)
			{
				for (dx = xstart; dx != xend; dx += xinc)
				{
					int addr	=	(code*2) & (TILES_LEN-1);
					int tile	=	TILES[addr+1] * 256 + TILES[addr];
	
					if (((x+dx*16+15) >= xmin) && ((x+dx*16) <= xmax) &&
						((y+dy*16+15) >= ymin) && ((y+dy*16) <= ymax))
						colmask[color] |= pen_usage[tile % total_elements];
	
					code++;
				}
			}
		}
	
		for (col = 0; col < total_color_codes; col++)
		 for (i = 0; i < 15; i++)	// pen 15 is transparent
		  if (colmask[col] & (1 << i))
		  {	palette_used_colors[16 * col + i + color_codes_start] = PALETTE_COLOR_USED;
			count++;	}
	
	#if 0
	{	char buf[80];
		sprintf(buf,"%d",count);
		usrintf_showmessage(buf);	}
	#endif
	}
	
	
	
	
	/***************************************************************************
	
									Screen Drawing
	
	***************************************************************************/
	
	public static VhUpdatePtr psikyo_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		/* Layers enable (not quite right) */
	
		tilemap_set_enable(tilemap_0, ~psikyo_vregs[ 0x412/2 ] & 1);
		tilemap_set_enable(tilemap_1, ~psikyo_vregs[ 0x416/2 ] & 1);
	
		/* Layers scrolling */
	
		tilemap_set_scrolly(tilemap_0, 0, psikyo_vregs[ 0x402/2 ] );
		tilemap_set_scrollx(tilemap_0, 0, psikyo_vregs[ 0x406/2 ] );
	
		tilemap_set_scrolly(tilemap_1, 0, psikyo_vregs[ 0x40a/2 ] );
		tilemap_set_scrollx(tilemap_1, 0, psikyo_vregs[ 0x40e/2 ] );
	
	
		tilemap_update(ALL_TILEMAPS);
	
		palette_init_used_colors();
	
		psikyo_mark_sprite_colors();
	
		palette_recalc();
	
	
		fillbitmap(bitmap,palette_transparent_pen,&Machine.visible_area);
	
		fillbitmap(priority_bitmap,0,NULL);
	
		tilemap_draw(bitmap,tilemap_0, TILEMAP_IGNORE_TRANSPARENCY, 0);
		tilemap_draw(bitmap,tilemap_1, 0,                           1);
	
		/* Sprites can go below layer 1 (and 0?) */
		psikyo_draw_sprites(bitmap);
	} };
}

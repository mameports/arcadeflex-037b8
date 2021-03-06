/***************************************************************************

  stadhero video emulation - Bryan McPhail, mish@tendril.co.uk

*********************************************************************

	MXC-06 chip to produce sprites, see dec0.c
	BAC-06 chip for background?

***************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class stadhero
{
	
	data16_t *stadhero_pf1_data,*stadhero_pf2_data;
	static struct tilemap *pf1_tilemap,*pf2_tilemap;
	static int flipscreen;
	
	static data16_t stadhero_pf2_control_0[8];
	static data16_t stadhero_pf2_control_1[8];
	
	/******************************************************************************/
	
	static void stadhero_drawsprites(struct osd_bitmap *bitmap,int pri_mask,int pri_val)
	{
		int offs;
	
		for (offs = 0;offs < 0x400;offs += 4)
		{
			int x,y,sprite,colour,multi,fx,fy,inc,flash,mult;
	
			y = spriteram16[offs];
			if ((y&0x8000) == 0) continue;
	
			x = spriteram16[offs+2];
			colour = x >> 12;
			if ((colour & pri_mask) != pri_val) continue;
	
			flash=x&0x800;
			if (flash && (cpu_getcurrentframe() & 1)) continue;
	
			fx = y & 0x2000;
			fy = y & 0x4000;
			multi = (1 << ((y & 0x1800) >> 11)) - 1;	/* 1x, 2x, 4x, 8x height */
												/* multi = 0   1   3   7 */
	
			sprite = spriteram16[offs+1] & 0x0fff;
	
			x = x & 0x01ff;
			y = y & 0x01ff;
			if (x >= 256) x -= 512;
			if (y >= 256) y -= 512;
			x = 240 - x;
			y = 240 - y;
	
			sprite &= ~multi;
			if (fy != 0)
				inc = -1;
			else
			{
				sprite += multi;
				inc = 1;
			}
	
			if (flip_screen != 0) {
				y=240-y;
				x=240-x;
				if (fx != 0) fx=0; else fx=1;
				if (fy != 0) fy=0; else fy=1;
				mult=16;
			}
			else mult=-16;
	
			while (multi >= 0)
			{
				drawgfx(bitmap,Machine.gfx[2],
						sprite - multi * inc,
						colour,
						fx,fy,
						x,y + mult * multi,
						&Machine.visible_area,TRANSPARENCY_PEN,0);
				multi--;
			}
		}
	}
	
	/******************************************************************************/
	
	public static VhUpdatePtr stadhero_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int offs;
		int color,i;
		int colmask[16];
		int pal_base;
	
		flipscreen=stadhero_pf2_control_0[0]&0x80;
		tilemap_set_flip(ALL_TILEMAPS,flipscreen ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);
		tilemap_set_scrollx( pf2_tilemap,0, stadhero_pf2_control_1[0] );
		tilemap_set_scrolly( pf2_tilemap,0, stadhero_pf2_control_1[1] );
	
		tilemap_update(pf2_tilemap);
		tilemap_update(pf1_tilemap);
		palette_init_used_colors();
	
		pal_base = Machine.drv.gfxdecodeinfo[2].color_codes_start;
		for (color = 0;color < 16;color++) colmask[color] = 0;
		for (offs = 0;offs < 0x400;offs += 4)
		{
			int x,y,sprite,multi;
	
			y = spriteram16[offs];
			if ((y&0x8000) == 0) continue;
	
			x = spriteram16[offs+2];
			color = (x & 0xf000) >> 12;
	
			multi = (1 << ((y & 0x1800) >> 11)) - 1;	/* 1x, 2x, 4x, 8x height */
												/* multi = 0   1   3   7 */
	
			sprite = spriteram16[offs+1] & 0x0fff;
			sprite &= ~multi;
	
			while (multi >= 0)
			{
				colmask[color] |= Machine.gfx[2].pen_usage[sprite + multi];
				multi--;
			}
		}
	
		for (color = 0;color < 16;color++)
		{
			for (i = 1;i < 16;i++)
			{
				if (colmask[color] & (1 << i))
					palette_used_colors[pal_base + 16 * color + i] = PALETTE_COLOR_USED;
			}
		}
	
		palette_recalc();
		tilemap_draw(bitmap,pf2_tilemap,0,0);
		stadhero_drawsprites(bitmap,0x00,0x00);
		tilemap_draw(bitmap,pf1_tilemap,0,0);
	} };
	
	/******************************************************************************/
	
	WRITE16_HANDLER( stadhero_pf1_data_w )
	{
		data16_t oldword=stadhero_pf1_data[offset];
		COMBINE_DATA(&stadhero_pf1_data[offset]);
		if (oldword!=stadhero_pf1_data[offset])
			tilemap_mark_tile_dirty(pf1_tilemap,offset);
	}
	
	WRITE16_HANDLER( stadhero_pf2_data_w )
	{
		data16_t oldword=stadhero_pf2_data[offset];
		COMBINE_DATA(&stadhero_pf2_data[offset]);
		if (oldword!=stadhero_pf2_data[offset])
			tilemap_mark_tile_dirty(pf2_tilemap,offset);
	}
	
	WRITE16_HANDLER( stadhero_pf2_control_0_w )
	{
		COMBINE_DATA(&stadhero_pf2_control_0[offset]);
	}
	
	WRITE16_HANDLER( stadhero_pf2_control_1_w )
	{
		COMBINE_DATA(&stadhero_pf2_control_1[offset]);
	}
	
	/******************************************************************************/
	
	static UINT32 stadhero_scan(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		/* logical (col,row) . memory offset */
		return (col & 0xf) + ((row & 0xf) << 4) + ((row & 0x30) << 4) + ((col & 0x30) << 6);
	}
	
	public static GetTileInfoPtr get_pf2_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int tile,color;
	
		tile=stadhero_pf2_data[tile_index];
		color=tile >> 12;
		tile=tile&0xfff;
	
		SET_TILE_INFO(1,tile,color)
	} };
	
	public static GetTileInfoPtr get_pf1_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int tile=stadhero_pf1_data[tile_index];
		int color=tile >> 12;
	
		tile=tile&0xfff;
		SET_TILE_INFO(0,tile,color)
	} };
	
	public static VhStartPtr stadhero_vh_start = new VhStartPtr() { public int handler() 
	{
		pf1_tilemap =     tilemap_create(get_pf1_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT, 8, 8,32,32);
		pf2_tilemap =     tilemap_create(get_pf2_tile_info,stadhero_scan,TILEMAP_OPAQUE,     16,16,64,64);
	
		if (!pf1_tilemap || !pf2_tilemap)
			return 1;
	
		tilemap_set_transparent_pen(pf1_tilemap,0);
	
		return 0;
	} };
	
	/******************************************************************************/
}

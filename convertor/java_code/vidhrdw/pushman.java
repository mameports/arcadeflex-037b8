/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class pushman
{
	
	static struct tilemap *bg_tilemap,*tx_tilemap;
	static data16_t control[2];
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static UINT32 background_scan_rows(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		/* logical (col,row) . memory offset */
		return ((col & 0x7)) + ((7-(row & 0x7)) << 3) + ((col & 0x78) <<3) + ((0x38-(row&0x38))<<7);
	}
	
	public static GetTileInfoPtr get_back_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		UBytePtr bgMap = memory_region(REGION_GFX4);
		int tile;
	
		tile=bgMap[tile_index<<1]+(bgMap[(tile_index<<1)+1]<<8);
		SET_TILE_INFO(2,(tile&0xff)|((tile&0x4000)>>6),(tile>>8)&0xf)
		tile_info.flags = ((tile&0x2000) >> 13);  /* flip x*/
	} };
	
	public static GetTileInfoPtr get_text_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int tile = videoram16[tile_index];
		SET_TILE_INFO(0,(tile&0xff)|((tile&0x8000)>>6)|((tile&0x4000)>>6)|((tile&0x2000)>>4),(tile>>8)&0xf)
	} };
	
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr pushman_vh_start = new VhStartPtr() { public int handler() 
	{
		bg_tilemap = tilemap_create(get_back_tile_info,background_scan_rows,TILEMAP_OPAQUE,     32,32,128,64);
		tx_tilemap = tilemap_create(get_text_tile_info,tilemap_scan_rows,   TILEMAP_TRANSPARENT, 8, 8, 32,32);
	
		if (!tx_tilemap || !bg_tilemap)
			return 1;
	
		tilemap_set_transparent_pen(tx_tilemap,3);
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	WRITE16_HANDLER( pushman_scroll_w )
	{
		COMBINE_DATA(&control[offset]);
	}
	
	WRITE16_HANDLER( pushman_videoram_w )
	{
		int oldword = videoram16[offset];
	
		COMBINE_DATA(&videoram16[offset]);
		if (oldword != videoram16[offset])
			tilemap_mark_tile_dirty(tx_tilemap,offset);
	}
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	static void draw_sprites(struct osd_bitmap *bitmap)
	{
		int offs,x,y,color,sprite;
	
		for (offs = 0x0800-4;offs >=0;offs -= 4)
		{
			/* Don't draw empty sprite table entries */
			x = spriteram16[offs+3]&0x1ff;
			if (x==0x180) continue;
			if (x>0xff) x=0-(0x200-x);
			y = 240-spriteram16[offs+2];
			color = ((spriteram16[offs+1]>>2)&0xf);
			sprite = spriteram16[offs]&0x7ff;
	
			drawgfx(bitmap,Machine.gfx[1],
					sprite,
					color,0,0,x,y,
					&Machine.visible_area,TRANSPARENCY_PEN,15);
		}
	}
	
	static void mark_sprite_colors(void)
	{
		int color,offs,sprite;
		int colmask[16],i,pal_base;
	
	
		pal_base = Machine.drv.gfxdecodeinfo[1].color_codes_start;
		for (color = 0;color < 16;color++) colmask[color] = 0;
		for (offs = 0;offs < 0x0800;offs += 4)
		{
			color = ((spriteram16[offs+1]>>2)&0xf);
			sprite = spriteram16[offs]&0x7ff;
			colmask[color] |= Machine.gfx[1].pen_usage[sprite];
		}
		for (color = 0;color < 16;color++)
		{
			for (i = 0;i < 16;i++)
			{
				if (colmask[color] & (1 << i))
					palette_used_colors[pal_base + 16 * color + i] = PALETTE_COLOR_USED;
			}
		}
	}
	
	public static VhUpdatePtr pushman_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		/* Setup the tilemaps */
		tilemap_set_scrollx( bg_tilemap,0, control[0] );
		tilemap_set_scrolly( bg_tilemap,0, 0xf00-control[1] );
		tilemap_update(ALL_TILEMAPS);
	
		/* Build the dynamic palette */
		palette_init_used_colors();
		mark_sprite_colors();
	
		palette_recalc();
	
		tilemap_draw(bitmap,bg_tilemap,0,0);
		draw_sprites(bitmap);
		tilemap_draw(bitmap,tx_tilemap,0,0);
	} };
}

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class spdodgeb
{
	
	
	UBytePtr spdodgeb_videoram;
	
	static int tile_palbank;
	static int sprite_palbank;
	static int scrollx[30];
	
	static struct tilemap *bg_tilemap;
	
	
	
	public static VhConvertColorPromPtr spdodgeb_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
	
	
		for (i = 0;i < Machine.drv.total_colors;i++)
		{
			int bit0,bit1,bit2,bit3;
	
	
			/* red component */
			bit0 = (color_prom.read(0)>> 0) & 0x01;
			bit1 = (color_prom.read(0)>> 1) & 0x01;
			bit2 = (color_prom.read(0)>> 2) & 0x01;
			bit3 = (color_prom.read(0)>> 3) & 0x01;
			*(palette++) = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* green component */
			bit0 = (color_prom.read(0)>> 4) & 0x01;
			bit1 = (color_prom.read(0)>> 5) & 0x01;
			bit2 = (color_prom.read(0)>> 6) & 0x01;
			bit3 = (color_prom.read(0)>> 7) & 0x01;
			*(palette++) = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
			/* blue component */
			bit0 = (color_prom.read(Machine->drv->total_colors)>> 0) & 0x01;
			bit1 = (color_prom.read(Machine->drv->total_colors)>> 1) & 0x01;
			bit2 = (color_prom.read(Machine->drv->total_colors)>> 2) & 0x01;
			bit3 = (color_prom.read(Machine->drv->total_colors)>> 3) & 0x01;
			*(palette++) = 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3;
	
			color_prom++;
		}
	} };
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static UINT32 background_scan(UINT32 col,UINT32 row,UINT32 num_cols,UINT32 num_rows)
	{
		/* logical (col,row) . memory offset */
		return (col & 0x1f) + ((row & 0x1f) << 5) + ((col & 0x20) << 5);
	}
	
	public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		unsigned char code = spdodgeb_videoram[tile_index];
		unsigned char attr = spdodgeb_videoram[tile_index + 0x800];
		SET_TILE_INFO(0,code + ((attr & 0x1f) << 8),((attr & 0xe0) >> 5) + 8 * tile_palbank)
	} };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr spdodgeb_vh_start = new VhStartPtr() { public int handler() 
	{
		bg_tilemap = tilemap_create(get_bg_tile_info,background_scan,TILEMAP_OPAQUE,8,8,64,32);
	
		if (!bg_tilemap)
			return 1;
	
		tilemap_set_scroll_rows(bg_tilemap,32);
	
		return 0;
	} };
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	static int lastscroll;
	
	public static InterruptPtr spdodgeb_interrupt = new InterruptPtr() { public int handler() 
	{
		int line = 33 - cpu_getiloops();
	
		if (line < 30)
		{
			scrollx[line] = lastscroll;
			return M6502_INT_IRQ;
		}
		else if (line == 30)	/* vblank */
			return M6502_INT_NMI;
		else 	/* skip 31 32 33 to allow vblank to finish */
			return ignore_interrupt();
	} };
	
	public static WriteHandlerPtr spdodgeb_scrollx_lo_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		lastscroll = (lastscroll & 0x100) | data;
	} };
	
	public static WriteHandlerPtr spdodgeb_ctrl_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		const UINT8 *rom = memory_region(REGION_CPU1);
	
		/* bit 0 = flip screen */
		flip_screen_set(data & 0x01);
	
		/* bit 1 = ROM bank switch */
		cpu_setbank(1,rom + 0x10000 + 0x4000 * ((~data & 0x02) >> 1));
	
		/* bit 2 = scroll high bit */
		lastscroll = (lastscroll & 0x0ff) | ((data & 0x04) << 6);
	
		/* bit 3 = to mcu?? */
	
		/* bits 4-7 = palette bank select */
		if (tile_palbank != ((data & 0x30) >> 4))
		{
			tile_palbank = ((data & 0x30) >> 4);
			tilemap_mark_all_tiles_dirty(bg_tilemap);
		}
		sprite_palbank = (data & 0xc0) >> 6;
	} };
	
	public static WriteHandlerPtr spdodgeb_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (spdodgeb_videoram[offset] != data)
		{
			spdodgeb_videoram[offset] = data;
			tilemap_mark_tile_dirty(bg_tilemap,offset & 0x7ff);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	#define DRAW_SPRITE( order, sx, sy ) drawgfx( bitmap, gfx, \
						(which+order),color+ 8 * sprite_palbank,flipx,flipy,sx,sy, \
						clip,TRANSPARENCY_PEN,0);
	
	static void draw_sprites( struct osd_bitmap *bitmap )
	{
		const struct rectangle *clip = &Machine.visible_area;
		const struct GfxElement *gfx = Machine.gfx[1];
		UBytePtr src;
		int i;
	
		src = spriteram;
	
	/*	240-Y    S|X|CLR|WCH WHICH    240-X
		xxxxxxxx x|x|xxx|xxx xxxxxxxx xxxxxxxx
	*/
	
	
		for (i = 0;i < spriteram_size;i += 4)
		{
			int attr = src[i+1];
			int which = src[i+2]+((attr & 0x07)<<8);
			int sx = ((src[i+3] + 8) & 0xff) - 8;
			int sy = 240 - src[i];
			int size = (attr & 0x80) >> 7;
			int color = (attr & 0x38) >> 3;
			int flipx = ~attr & 0x40;
			int flipy = 0;
			int dy = -16;
	
			if (flip_screen != 0)
			{
				sx = 240 - sx;
				sy = 240 - sy;
				flipx = !flipx;
				flipy = !flipy;
				dy = -dy;
			}
	
			switch (size)
			{
				case 0: /* normal */
				DRAW_SPRITE(0,sx,sy);
				break;
	
				case 1: /* double y */
				DRAW_SPRITE(0,sx,sy + dy);
				DRAW_SPRITE(1,sx,sy);
				break;
			}
		}
	}
	
	#undef DRAW_SPRITE
	
	
	public static VhUpdatePtr spdodgeb_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int i;
	
	
		if (flip_screen != 0)
		{
			for (i = 0;i < 30;i++)
				tilemap_set_scrollx(bg_tilemap,i+1,scrollx[29 - i]+5);
		}
		else
		{
			for (i = 0;i < 30;i++)
				tilemap_set_scrollx(bg_tilemap,i+1,scrollx[i]+5);
		}
	
		tilemap_update(ALL_TILEMAPS);
	
		palette_init_used_colors();
		/* mark sprites colors */
		for (i = 0;i < 128;i++)
			palette_used_colors[sprite_palbank*0x80 + i + 0x200] |= PALETTE_COLOR_VISIBLE;
	
		palette_recalc();
	
		tilemap_draw(bitmap,bg_tilemap,0,0);
		draw_sprites(bitmap);
	} };
}

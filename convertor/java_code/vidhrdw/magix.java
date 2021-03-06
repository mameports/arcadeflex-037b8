/***************************************************************************

								-=  Magix  =-
							 (c) 1995  Yun Sung

				driver by	Luca Elia (eliavit@unina.it)


Note:	if MAME_DEBUG is defined, pressing Z with:

		Q 		shows the background layer
		W 		shows the foreground layer

		[ 2 Fixed Layers ]

			[ Background ]

			Layer Size:				512 x 256
			Tiles:					6 x 8 x 8 (!)

			[ Foreground ]

			Layer Size:				512 x 256
			Tiles:					8 x 8 x 4


		There are no sprites.

***************************************************************************/
/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class magix
{
	
	
	/* Variables that driver has access to: */
	
	UBytePtr magix_videoram_0,*magix_videoram_1;
	
	
	/* Variables only used here: */
	
	static struct tilemap *tilemap_0, *tilemap_1;
	static int magix_videobank;
	
	
	/***************************************************************************
	
								Memory Handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr magix_videobank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		magix_videobank = data;
	} };
	
	
	public static ReadHandlerPtr magix_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int bank;
	
		/*	Bit 1 of the bankswitching register contols the c000-c7ff
			area (Palette). Bit 0 controls the c800-dfff area (Tiles) */
	
		if (offset < 0x0800)	bank = magix_videobank & 2;
		else					bank = magix_videobank & 1;
	
		if (bank != 0)	return magix_videoram_0[offset];
		else		return magix_videoram_1[offset];
	} };
	
	
	public static WriteHandlerPtr magix_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (offset < 0x0800)		// c000-c7ff	Banked Palette RAM
		{
			int bank = magix_videobank & 2;
			UBytePtr RAM;
			int r,g,b;
	
			if (bank != 0)	RAM = magix_videoram_0;
			else		RAM = magix_videoram_1;
	
			RAM[offset] = data;
			data = RAM[offset & ~1] | (RAM[offset | 1] << 8);
	
			/* BBBBBGGGGGRRRRRx */
			r = (data >>  0) & 0x1f;
			g = (data >>  5) & 0x1f;
			b = (data >> 10) & 0x1f;
	
			palette_change_color(offset/2 + (bank ? 0x400:0), (r << 3)|(r >> 2), (g << 3)|(g >> 2), (b << 3)|(b >> 2));
		}
		else
		{
			int tile;
			int bank = magix_videobank & 1;
	
			if (offset < 0x1000)	tile = (offset-0x0800);		// c800-cfff: Banked Color RAM
			else				 	tile = (offset-0x1000)/2;	// d000-dfff: Banked Tiles RAM
	
			if (bank != 0)	{	magix_videoram_0[offset] = data;
							tilemap_mark_tile_dirty(tilemap_0, tile);	}
			else		{	magix_videoram_1[offset] = data;
							tilemap_mark_tile_dirty(tilemap_1, tile);	}
		}
	} };
	
	
	public static WriteHandlerPtr magix_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		tilemap_set_flip(ALL_TILEMAPS, (data & 1) ? (TILEMAP_FLIPX|TILEMAP_FLIPY) : 0);
	} };
	
	
	
	
	/***************************************************************************
	
							Callbacks for the TileMap code
	
	***************************************************************************/
	
	
	/***************************************************************************
	
								  [ Tiles Format ]
	
	Offset:
	
		Videoram + 0000.w		Code
		Colorram + 0000.b		Color
	
	
	***************************************************************************/
	
	/* Background */
	
	#define DIM_NX_0			(0x40)
	#define DIM_NY_0			(0x20)
	
	public static GetTileInfoPtr get_tile_info_0 = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int code  =  magix_videoram_0[0x1000+tile_index * 2 + 0] + magix_videoram_0[0x1000+tile_index * 2 + 1] * 256;
		int color =  magix_videoram_0[0x0800+ tile_index] & 0x07;
		SET_TILE_INFO( 0, code, color );
	} };
	
	/* Text Plane */
	
	#define DIM_NX_1			(0x40)
	#define DIM_NY_1			(0x20)
	
	public static GetTileInfoPtr get_tile_info_1 = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int code  =  magix_videoram_1[0x1000+ tile_index * 2 + 0] + magix_videoram_1[0x1000+tile_index * 2 + 1] * 256;
		int color =  magix_videoram_1[0x0800+ tile_index] & 0x3f;
		SET_TILE_INFO( 1, code, color );
	} };
	
	
	
	
	/***************************************************************************
	
	
									Vh_Start
	
	
	***************************************************************************/
	
	public static VhStartPtr magix_vh_start = new VhStartPtr() { public int handler() 
	{
		tilemap_0 = tilemap_create(	get_tile_info_0,
									tilemap_scan_rows,
									TILEMAP_OPAQUE,
									8,8,
									DIM_NX_0, DIM_NY_0 );
	
		tilemap_1 = tilemap_create(	get_tile_info_1,
									tilemap_scan_rows,
									TILEMAP_TRANSPARENT,
									8,8,
									DIM_NX_1, DIM_NY_1 );
	
		if (tilemap_0 && tilemap_1)
		{
			tilemap_set_transparent_pen(tilemap_1,0);
			return 0;
		}
		else return 1;
	} };
	
	
	
	/***************************************************************************
	
	
									Screen Drawing
	
	
	***************************************************************************/
	
	public static VhUpdatePtr magix_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int layers_ctrl = -1;
	
	#ifdef MAME_DEBUG
	if (keyboard_pressed(KEYCODE_Z))
	{
		int msk = 0;
		if (keyboard_pressed(KEYCODE_Q))	msk |= 1;
		if (keyboard_pressed(KEYCODE_W))	msk |= 2;
		if (msk != 0) layers_ctrl &= msk;
	}
	#endif
	
		tilemap_update(ALL_TILEMAPS);
	
		palette_init_used_colors();
	
		/* No Sprites ... */
	
		palette_recalc();
	
		if ((layers_ctrl & 1) != 0)	tilemap_draw(bitmap, tilemap_0, 0,0);
		else                fillbitmap(bitmap,palette_transparent_pen,&Machine.visible_area);
	
		if ((layers_ctrl & 2) != 0)	tilemap_draw(bitmap, tilemap_1, 0,0);
	} };
}

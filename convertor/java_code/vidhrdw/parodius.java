/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class parodius
{
	
	
	static int layer_colorbase[3],sprite_colorbase,bg_colorbase;
	static int layerpri[3];
	
	
	/***************************************************************************
	
	  Callbacks for the K052109
	
	***************************************************************************/
	
	public static K052109_callbackProcPtr tile_callback = new K052109_callbackProcPtr() { public void handler(int layer,int bank,int[] code,int[] color) 
	{
		*code |= ((*color & 0x03) << 8) | ((*color & 0x10) << 6) | ((*color & 0x0c) << 9) | (bank << 13);
		*color = layer_colorbase[layer] + ((*color & 0xe0) >> 5);
	} };
	
	/***************************************************************************
	
	  Callbacks for the K053245
	
	***************************************************************************/
	
	static void sprite_callback(int *code,int *color,int *priority_mask)
	{
		int pri = 0x20 | ((*color & 0x60) >> 2);
		if (pri <= layerpri[2])								*priority_mask = 0;
		else if (pri > layerpri[2] && pri <= layerpri[1])	*priority_mask = 0xf0;
		else if (pri > layerpri[1] && pri <= layerpri[0])	*priority_mask = 0xf0|0xcc;
		else 												*priority_mask = 0xf0|0xcc|0xaa;
	
		*color = sprite_colorbase + (*color & 0x1f);
	}
	
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr parodius_vh_start = new VhStartPtr() { public int handler() 
	{
		if (K052109_vh_start(REGION_GFX1,NORMAL_PLANE_ORDER,tile_callback))
		{
			return 1;
		}
		if (K053245_vh_start(REGION_GFX2,NORMAL_PLANE_ORDER,sprite_callback))
		{
			K052109_vh_stop();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStopPtr parodius_vh_stop = new VhStopPtr() { public void handler() 
	{
		K052109_vh_stop();
		K053245_vh_stop();
	} };
	
	/* useful function to sort the three tile layers by priority order */
	static void sortlayers(int *layer,int *pri)
	{
	#define SWAP(a,b) \
		if (pri[a] < pri[b]) \
		{ \
			int t; \
			t = pri[a]; pri[a] = pri[b]; pri[b] = t; \
			t = layer[a]; layer[a] = layer[b]; layer[b] = t; \
		}
	
		SWAP(0,1)
		SWAP(0,2)
		SWAP(1,2)
	}
	
	public static VhUpdatePtr parodius_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int layer[3];
	
	
		bg_colorbase       = K053251_get_palette_index(K053251_CI0);
		sprite_colorbase   = K053251_get_palette_index(K053251_CI1);
		layer_colorbase[0] = K053251_get_palette_index(K053251_CI2);
		layer_colorbase[1] = K053251_get_palette_index(K053251_CI4);
		layer_colorbase[2] = K053251_get_palette_index(K053251_CI3);
	
		K052109_tilemap_update();
	
		palette_init_used_colors();
		K053245_mark_sprites_colors();
		palette_used_colors[16 * bg_colorbase] |= PALETTE_COLOR_VISIBLE;
		palette_recalc();
	
		layer[0] = 0;
		layerpri[0] = K053251_get_priority(K053251_CI2);
		layer[1] = 1;
		layerpri[1] = K053251_get_priority(K053251_CI4);
		layer[2] = 2;
		layerpri[2] = K053251_get_priority(K053251_CI3);
	
		sortlayers(layer,layerpri);
	
		fillbitmap(priority_bitmap,0,NULL);
		fillbitmap(bitmap,Machine.pens[16 * bg_colorbase],&Machine.visible_area);
		K052109_tilemap_draw(bitmap,layer[0],0,1);
		K052109_tilemap_draw(bitmap,layer[1],0,2);
		K052109_tilemap_draw(bitmap,layer[2],0,4);
	
		K053245_sprites_draw(bitmap);
	} };
}

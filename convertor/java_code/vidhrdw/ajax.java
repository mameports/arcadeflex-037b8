/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class ajax
{
	
	
	unsigned char ajax_priority;
	static int layer_colorbase[3],sprite_colorbase,zoom_colorbase;
	
	
	/***************************************************************************
	
	  Callbacks for the K052109
	
	***************************************************************************/
	
	public static K052109_callbackProcPtr tile_callback = new K052109_callbackProcPtr() { public void handler(int layer,int bank,int[] code,int[] color) 
	{
		*code |= ((*color & 0x0f) << 8) | (bank << 12);
		*color = layer_colorbase[layer] + ((*color & 0xf0) >> 4);
	} };
	
	
	/***************************************************************************
	
	  Callbacks for the K051960
	
	***************************************************************************/
	
	static void sprite_callback(int *code,int *color,int *priority,int *shadow)
	{
		/* priority bits:
		   4 over zoom (0 = have priority)
		   5 over B    (0 = have priority)
		   6 over A    (1 = have priority)
		   never over F
		*/
		*priority = 0xff00;							/* F = 8 */
		if ( *color & 0x10) *priority |= 0xf0f0;	/* Z = 4 */
		if (~*color & 0x40) *priority |= 0xcccc;	/* A = 2 */
		if ( *color & 0x20) *priority |= 0xaaaa;	/* B = 1 */
		*color = sprite_colorbase + (*color & 0x0f);
	}
	
	
	/***************************************************************************
	
	  Callbacks for the K051316
	
	***************************************************************************/
	
	static void zoom_callback(int *code,int *color)
	{
		*code |= ((*color & 0x07) << 8);
		*color = zoom_colorbase + ((*color & 0x08) >> 3);
	}
	
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr ajax_vh_start = new VhStartPtr() { public int handler() 
	{
		layer_colorbase[0] = 64;
		layer_colorbase[1] = 0;
		layer_colorbase[2] = 32;
		sprite_colorbase = 16;
		zoom_colorbase = 6;	/* == 48 since it's 7-bit graphics */
		if (K052109_vh_start(REGION_GFX1,NORMAL_PLANE_ORDER,tile_callback))
			return 1;
		if (K051960_vh_start(REGION_GFX2,NORMAL_PLANE_ORDER,sprite_callback))
		{
			K052109_vh_stop();
			return 1;
		}
		if (K051316_vh_start_0(REGION_GFX3,7,zoom_callback))
		{
			K052109_vh_stop();
			K051960_vh_stop();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStopPtr ajax_vh_stop = new VhStopPtr() { public void handler() 
	{
		K052109_vh_stop();
		K051960_vh_stop();
		K051316_vh_stop_0();
	} };
	
	
	
	/***************************************************************************
	
		Display Refresh
	
	***************************************************************************/
	
	public static VhUpdatePtr ajax_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
		K051316_tilemap_update_0();
	
		palette_init_used_colors();
		K051960_mark_sprites_colors();
		/* set back pen for the zoom layer */
		palette_used_colors[(zoom_colorbase + 0) * 128] = PALETTE_COLOR_TRANSPARENT;
		palette_used_colors[(zoom_colorbase + 1) * 128] = PALETTE_COLOR_TRANSPARENT;
		palette_recalc();
	
		fillbitmap(priority_bitmap,0,NULL);
	
		fillbitmap(bitmap,Machine.pens[0],&Machine.visible_area);
		K052109_tilemap_draw(bitmap,2,0,1);
		if (ajax_priority != 0)
		{
			/* basic layer order is B, zoom, A, F */
			K051316_zoom_draw_0(bitmap,4);
			K052109_tilemap_draw(bitmap,1,0,2);
		}
		else
		{
			/* basic layer order is B, A, zoom, F */
			K052109_tilemap_draw(bitmap,1,0,2);
			K051316_zoom_draw_0(bitmap,4);
		}
		K052109_tilemap_draw(bitmap,0,0,8);
	
		K051960_sprites_draw(bitmap,-1,-1);
	} };
}

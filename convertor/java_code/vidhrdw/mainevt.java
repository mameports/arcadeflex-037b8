/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class mainevt
{
	
	
	static int layer_colorbase[3],sprite_colorbase;
	
	
	
	/***************************************************************************
	
	  Callbacks for the K052109
	
	***************************************************************************/
	
	public static K052109_callbackProcPtr mainevt_tile_callback = new K052109_callbackProcPtr() { public void handler(int layer,int bank,int[] code,int[] color) 
	{
		tile_info.flags = (*color & 0x02) ? TILE_FLIPX : 0;
	
		/* priority relative to HALF priority sprites */
		if (layer == 2) tile_info.priority = (*color & 0x20) >> 5;
		else tile_info.priority = 0;
	
		*code |= ((*color & 0x01) << 8) | ((*color & 0x1c) << 7);
		*color = layer_colorbase[layer] + ((*color & 0xc0) >> 6);
	} };
	
	public static K052109_callbackProcPtr dv_tile_callback = new K052109_callbackProcPtr() { public void handler(int layer,int bank,int[] code,int[] color) 
	{
		/* (color & 0x02) is flip y handled internally by the 052109 */
		*code |= ((*color & 0x01) << 8) | ((*color & 0x3c) << 7);
		*color = layer_colorbase[layer] + ((*color & 0xc0) >> 6);
	} };
	
	
	/***************************************************************************
	
	  Callbacks for the K051960
	
	***************************************************************************/
	
	public static K051960_callbackProcPtr mainevt_sprite_callback = new K051960_callbackProcPtr() { public void handler(int[] code,int[] color,int[] priority) 
	{
		/* bit 5 = priority over layer B (has precedence) */
		/* bit 6 = HALF priority over layer B (used for crowd when you get out of the ring) */
		if (*color & 0x20)		*priority_mask = 0xff00;
		else if (*color & 0x40)	*priority_mask = 0xff00|0xf0f0;
		else					*priority_mask = 0xff00|0xf0f0|0xcccc;
		/* bit 7 is shadow, not used */
	
		*color = sprite_colorbase + (*color & 0x03);
	} };
	
	static void dv_sprite_callback(int *code,int *color,int *priority,int *shadow)
	{
		/* TODO: the priority/shadow handling (bits 5-7) seems to be quite complex (see PROM) */
		*color = sprite_colorbase + (*color & 0x07);
	}
	
	
	/*****************************************************************************/
	
	public static VhStartPtr mainevt_vh_start = new VhStartPtr() { public int handler() 
	{
		layer_colorbase[0] = 0;
		layer_colorbase[1] = 8;
		layer_colorbase[2] = 4;
		sprite_colorbase = 12;
	
		if (K052109_vh_start(REGION_GFX1,NORMAL_PLANE_ORDER,mainevt_tile_callback))
			return 1;
		if (K051960_vh_start(REGION_GFX2,NORMAL_PLANE_ORDER,mainevt_sprite_callback))
		{
			K052109_vh_stop();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStartPtr dv_vh_start = new VhStartPtr() { public int handler() 
	{
		layer_colorbase[0] = 0;
		layer_colorbase[1] = 0;
		layer_colorbase[2] = 4;
		sprite_colorbase = 8;
	
		if (K052109_vh_start(REGION_GFX1,NORMAL_PLANE_ORDER,dv_tile_callback))
			return 1;
		if (K051960_vh_start(REGION_GFX2,NORMAL_PLANE_ORDER,dv_sprite_callback))
		{
			K052109_vh_stop();
			return 1;
		}
	
		return 0;
	} };
	
	public static VhStopPtr mainevt_vh_stop = new VhStopPtr() { public void handler() 
	{
		K052109_vh_stop();
		K051960_vh_stop();
	} };
	
	/*****************************************************************************/
	
	public static VhUpdatePtr mainevt_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
	
		palette_recalc();
	
		fillbitmap(priority_bitmap,0,NULL);
		K052109_tilemap_draw(bitmap,1,TILEMAP_IGNORE_TRANSPARENCY,1);
		K052109_tilemap_draw(bitmap,2,1,2);	/* low priority part of layer */
		K052109_tilemap_draw(bitmap,2,0,4);	/* high priority part of layer */
		K052109_tilemap_draw(bitmap,0,0,8);
	
		K051960_sprites_draw(bitmap,-1,-1);
	} };
	
	public static VhUpdatePtr dv_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
	
		palette_recalc();
	
		K052109_tilemap_draw(bitmap,1,TILEMAP_IGNORE_TRANSPARENCY,0);
		K052109_tilemap_draw(bitmap,2,0,0);
		K051960_sprites_draw(bitmap,0,0);
		K052109_tilemap_draw(bitmap,0,0,0);
	} };
}

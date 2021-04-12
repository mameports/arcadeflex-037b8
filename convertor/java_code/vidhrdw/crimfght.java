/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class crimfght
{
	
	
	static int layer_colorbase[3],sprite_colorbase;
	
	/***************************************************************************
	
	  Callbacks for the K052109
	
	***************************************************************************/
	
	public static K052109_callbackProcPtr tile_callback = new K052109_callbackProcPtr() { public void handler(int layer,int bank,int[] code,int[] color) 
	{
		tile_info.flags = (*color & 0x20) ? TILE_FLIPX : 0;
		*code |= ((*color & 0x1f) << 8) | (bank << 13);
		*color = layer_colorbase[layer] + ((*color & 0xc0) >> 6);
	} };
	
	/***************************************************************************
	
	  Callbacks for the K051960
	
	***************************************************************************/
	
	static void sprite_callback(int *code,int *color,int *priority,int *shadow)
	{
		/* Weird priority scheme. Why use three bits when two would suffice? */
		/* The PROM allows for mixed priorities, where sprites would have */
		/* priority over text but not on one or both of the other two planes. */
		/* Luckily, this isn't used by the game. */
		switch (*color & 0x70)
		{
			case 0x10: *priority = 0; break;
			case 0x00: *priority = 1; break;
			case 0x40: *priority = 2; break;
			case 0x20: *priority = 3; break;
			/*   0x60 == 0x20 */
			/*   0x50 priority over F and A, but not over B */
			/*   0x30 priority over F, but not over A and B */
			/*   0x70 == 0x30 */
		}
		/* bit 7 is on in the "Game Over" sprites, meaning unknown */
		/* in Aliens it is the top bit of the code, but that's not needed here */
		*color = sprite_colorbase + (*color & 0x0f);
	}
	
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStopPtr crimfght_vh_stop = new VhStopPtr() { public void handler() 
	{
		free(paletteram);
		K052109_vh_stop();
		K051960_vh_stop();
	} };
	
	public static VhStartPtr crimfght_vh_start = new VhStartPtr() { public int handler() 
	{
		paletteram = malloc(0x400);
		if (!paletteram) return 1;
	
		layer_colorbase[0] = 0;
		layer_colorbase[1] = 4;
		layer_colorbase[2] = 8;
		sprite_colorbase = 16;
		if (K052109_vh_start(REGION_GFX1,NORMAL_PLANE_ORDER,tile_callback))
		{
			free(paletteram);
			return 1;
		}
		if (K051960_vh_start(REGION_GFX2,NORMAL_PLANE_ORDER,sprite_callback))
		{
			free(paletteram);
			K052109_vh_stop();
			return 1;
		}
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	public static VhUpdatePtr crimfght_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		K052109_tilemap_update();
	
		palette_init_used_colors();
		K051960_mark_sprites_colors();
		palette_recalc();
	
		K052109_tilemap_draw(bitmap,1,TILEMAP_IGNORE_TRANSPARENCY,0);
		K051960_sprites_draw(bitmap,2,2);
		K052109_tilemap_draw(bitmap,2,0,0);
		K051960_sprites_draw(bitmap,1,1);
		K052109_tilemap_draw(bitmap,0,0,0);
		K051960_sprites_draw(bitmap,0,0);
	} };
}

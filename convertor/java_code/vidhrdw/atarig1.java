/***************************************************************************

	Atari G1 hardware

****************************************************************************/


/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class atarig1
{
	
	
	
	/*************************************
	 *
	 *	Globals we own
	 *
	 *************************************/
	
	UINT8 atarig1_pitfight;
	
	
	
	/*************************************
	 *
	 *	Statics
	 *
	 *************************************/
	
	static int pfscroll_xoffset;
	static UINT16 current_control;
	
	
	
	/*************************************
	 *
	 *	Video system start
	 *
	 *************************************/
	
	public static VhStartPtr hydra_vh_start = new VhStartPtr() { public int handler() 
	{
		static const struct ataripf_desc pfdesc =
		{
			0,			/* index to which gfx system */
			64,64,		/* size of the playfield in tiles (x,y) */
			1,64,		/* tile_index = x * xmult + y * ymult (xmult,ymult) */
		
			0x300,		/* index of palette base */
			0x100,		/* maximum number of colors */
			0,			/* color XOR for shadow effect (if any) */
			0,			/* latch mask */
			0,			/* transparent pen mask */
		
			0x70fff,	/* tile data index mask */
			0x07000,	/* tile data color mask */
			0x08000,	/* tile data hflip mask */
			0,			/* tile data vflip mask */
			0			/* tile data priority mask */
		};
	
		static const struct atarirle_desc modesc_hydra =
		{
			REGION_GFX3,/* region where the GFX data lives */
			256,		/* number of entries in sprite RAM */
			0,			/* left clip coordinate */
			255,		/* right clip coordinate */
			
			0x200,		/* base palette entry */
			0x100,		/* maximum number of colors */
		
			{{ 0x7fff,0,0,0,0,0,0,0 }},	/* mask for the code index */
			{{ 0,0x00f0,0,0,0,0,0,0 }},	/* mask for the color */
			{{ 0,0,0xffc0,0,0,0,0,0 }},	/* mask for the X position */
			{{ 0,0,0,0xffc0,0,0,0,0 }},	/* mask for the Y position */
			{{ 0,0,0,0,0xffff,0,0,0 }},	/* mask for the scale factor */
			{{ 0x8000,0,0,0,0,0,0,0 }},	/* mask for the horizontal flip */
			{{ 0 }},					/* mask for the vertical flip */
			{{ 0,0,0,0,0,0x00ff,0,0 }}	/* mask for the priority */
		};
	
		static const struct atarirle_desc modesc_pitfight =
		{
			REGION_GFX3,/* region where the GFX data lives */
			256,		/* number of entries in sprite RAM */
			40,			/* left clip coordinate */
			295,		/* right clip coordinate */
			
			0x200,		/* base palette entry */
			0x100,		/* maximum number of colors */
		
			{{ 0x7fff,0,0,0,0,0,0,0 }},	/* mask for the code index */
			{{ 0,0x00f0,0,0,0,0,0,0 }},	/* mask for the color */
			{{ 0,0,0xffc0,0,0,0,0,0 }},	/* mask for the X position */
			{{ 0,0,0,0xffc0,0,0,0,0 }},	/* mask for the Y position */
			{{ 0,0,0,0,0xffff,0,0,0 }},	/* mask for the scale factor */
			{{ 0x8000,0,0,0,0,0,0,0 }},	/* mask for the horizontal flip */
			{{ 0 }},					/* mask for the vertical flip */
			{{ 0,0,0,0,0,0,0x00ff,0 }}	/* mask for the priority */
		};
	
		static const struct atarian_desc andesc =
		{
			1,			/* index to which gfx system */
			64,32,		/* size of the alpha RAM in tiles (x,y) */
		
			0x100,		/* index of palette base */
			0x100,		/* maximum number of colors */
			0,			/* mask of the palette split */
	
			0x0fff,		/* tile data index mask */
			0xf000,		/* tile data color mask */
			0,			/* tile data hflip mask */
			0x8000		/* tile data opacity mask */
		};
	
		/* blend the playfields and free the temporary one */
		ataripf_blend_gfx(0, 2, 0x0f, 0x10);
	
		/* initialize the playfield */
		if (!ataripf_init(0, &pfdesc))
			goto cant_create_pf;
		
		/* initialize the motion objects */
		if (!atarirle_init(0, atarig1_pitfight ? &modesc_pitfight : &modesc_hydra))
			goto cant_create_mo;
	
		/* initialize the alphanumerics */
		if (!atarian_init(0, &andesc))
			goto cant_create_an;
		
		/* reset statics */
		current_control = 0;
		pfscroll_xoffset = atarig1_pitfight ? 2 : 0;
		return 0;
	
		/* error cases */
	cant_create_an:
		atarirle_free();
	cant_create_mo:
		ataripf_free();
	cant_create_pf:
		return 1;
	} };
	
	
	
	/*************************************
	 *
	 *	Video system shutdown
	 *
	 *************************************/
	
	public static VhStopPtr hydra_vh_stop = new VhStopPtr() { public void handler() 
	{
		atarian_free();
		atarirle_free();
		ataripf_free();
	} };
	
	
	
	/*************************************
	 *
	 *	Periodic scanline updater
	 *
	 *************************************/
	
	WRITE16_HANDLER( hydra_mo_control_w )
	{
		logerror("MOCONT = %d (scan = %d)\n", data, cpu_getscanline());
	
		/* set the control value */
		COMBINE_DATA(&current_control);
	}
	
	
	void hydra_scanline_update(int scanline)
	{
		data16_t *base = &atarian_0_base[(scanline / 8) * 64 + 47];
		int i;
	
		if (scanline == 0) logerror("-------\n");
	
		/* keep in range */
		if (base >= &atarian_0_base[0x7c0])
			return;
	
		/* update the playfield scrolls */
		for (i = 0; i < 8; i++)
		{
			data16_t word;
	
			word = base[i * 2 + 1];
			if ((word & 0x8000) != 0)
				ataripf_set_xscroll(0, ((word >> 6) + pfscroll_xoffset) & 0x1ff, scanline + i);
	
			word = base[i * 2 + 2];
			if ((word & 0x8000) != 0)
			{
				ataripf_set_yscroll(0, ((word >> 6) - (scanline + i)) & 0x1ff, scanline + i);
				ataripf_set_bankbits(0, (word & 7) << 16, scanline + i);
			}
		}
	}
	
	
	
	/*************************************
	 *
	 *	Main refresh
	 *
	 *************************************/
	
	public static VhUpdatePtr hydra_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		/* mark the used colors */
		palette_init_used_colors();
		ataripf_mark_palette(0);
		atarirle_mark_palette(0);
		atarian_mark_palette(0);
	
		/* update the palette, and mark things dirty if we need to */
		if (palette_recalc())
			ataripf_invalidate(0);
	
		/* draw the layers */
		ataripf_render(0, bitmap);
		atarirle_render(0, bitmap, NULL);
		atarian_render(0, bitmap);
	} };
}

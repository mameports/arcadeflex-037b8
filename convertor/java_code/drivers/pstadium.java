/***************************************************************************

	Game Driver for Nichibutsu Mahjong series.

	Mahjong Triple Wars
	(c)1989 NihonBussan Co.,Ltd.

	Mahjong Panic Stadium
	(c)1990 NihonBussan Co.,Ltd.

	Mahjong Triple Wars 2
	(c)1990 NihonBussan Co.,Ltd.

	Mahjong Nerae! Top Star
	(c)1990 NihonBussan Co.,Ltd.

	Mahjong Jikken Love Story
	(c)1991 NihonBussan Co.,Ltd.

	Mahjong Vanilla Syndrome
	(c)1991 NihonBussan Co.,Ltd.

	Quiz-Mahjong Hayaku Yatteyo!
	(c)1991 NihonBussan Co.,Ltd.

	Mahjong Gal no Kokuhaku
	(c)1989 NihonBussan Co.,Ltd. / (c)1989 T.R.TEC

	Mahjong Gal no Kaika
	(c)1989 NihonBussan Co.,Ltd. / (c)1989 T.R.TEC

	Tokyo Gal Zukan
	(c)1989 NihonBussan Co.,Ltd.

	Tokimeki Bishoujo (Medal type)
	(c)1989 NihonBussan Co.,Ltd.

	Miss Mahjong Contest
	(c)1989 NihonBussan Co.,Ltd.

	AV2 Mahjong No.1 Bay Bridge no Seijo
	(c)1991 NihonBussan Co.,Ltd. / Miki Syouji / AV Japan

	Driver by Takahiro Nogi 1999/12/02 -

***************************************************************************/
/***************************************************************************
Memo:

>>>>>>>	When the game tries to flip the screen, the screen is shifted to the left.

>>>>>>>	Attract sound will be played even DIP setting is set to "OFF".

>>>>>>>	If "Game sound" is set to "OFF" in mjlstory, attract sound is not played
		even if "Attract sound" is set to "ON".

>>>>>>>	The program of galkaika, tokyogal, and tokimbsj runs on Interrupt mode 2
		on real machine, but they don't run correctly in MAME so I changed to
		interrupt mode 1.

>>>>>>>	Sound CPU of qmhayaku is running on 4MHz in real machine. But if I set
		it to 4MHz in MAME, sounds are not  played so I lowered the clock a bit.

>>>>>>>	av2mj1's VCR playback is not implemented.

***************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class pstadium
{
	
	
	public static WriteHandlerPtr pstadium_soundbank_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		UBytePtr RAM = memory_region(REGION_CPU2);
	
		if (!(data & 0x80)) soundlatch_clear_w(0, 0);
		cpu_setbank(1, &RAM[0x08000 + (0x8000 * (data & 0x03))]);
	} };
	
	public static WriteHandlerPtr pstadium_sound_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		soundlatch_w.handler(0, data);
	} };
	
	public static ReadHandlerPtr pstadium_sound_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int data;
	
		data = soundlatch_r(0);
		soundlatch_clear_w(0, 0);
		return data;
	} };
	
	static public static InitDriverPtr init_pstadium = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_PSTADIUM;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_triplew1 = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_TRIPLEW1;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_triplew2 = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_TRIPLEW2;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_ntopstar = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_NTOPSTAR;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_mjlstory = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_MJLSTORY;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_vanilla = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_VANILLA;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_qmhayaku = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_QMHAYAKU;
		nb1413m3_int_count = 0;
	} };
	
	static public static InitDriverPtr init_galkoku = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_GALKOKU;
		nb1413m3_int_count = 128;
	} };
	
	static public static InitDriverPtr init_galkaika = new InitDriverPtr() { public void handler() 
	{
	#if 1
		UBytePtr ROM = memory_region(REGION_CPU1);
	
		// Patch to IM2 . IM1
		ROM[0x0002] = 0x56;
	#endif
		nb1413m3_type = NB1413M3_GALKAIKA;
		nb1413m3_int_count = 128;
	} };
	
	static public static InitDriverPtr init_tokyogal = new InitDriverPtr() { public void handler() 
	{
	#if 1
		UBytePtr ROM = memory_region(REGION_CPU1);
	
		// Patch to IM2 . IM1
		ROM[0x0002] = 0x56;
	#endif
		nb1413m3_type = NB1413M3_TOKYOGAL;
		nb1413m3_int_count = 128;
	} };
	
	static public static InitDriverPtr init_tokimbsj = new InitDriverPtr() { public void handler() 
	{
	#if 1
		UBytePtr ROM = memory_region(REGION_CPU1);
	
		// Patch to IM2 . IM1
		ROM[0x0002] = 0x56;
	#endif
		nb1413m3_type = NB1413M3_TOKIMBSJ;
		nb1413m3_int_count = 128;
	} };
	
	static public static InitDriverPtr init_mcontest = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_MCONTEST;
		nb1413m3_int_count = 128;
	} };
	
	static public static InitDriverPtr init_av2mj1 = new InitDriverPtr() { public void handler() 
	{
		nb1413m3_type = NB1413M3_AV2MJ1;
		nb1413m3_int_count = 0;
	} };
	
	
	public static Memory_ReadAddress readmem_pstadium[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf00f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf200, 0xf3ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_pstadium[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf00f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf200, 0xf3ff, pstadium_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_triplew1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf200, 0xf20f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_triplew1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, pstadium_palette_w ),
		new Memory_WriteAddress( 0xf200, 0xf20f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_triplew2[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf400, 0xf40f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_triplew2[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, pstadium_palette_w ),
		new Memory_WriteAddress( 0xf400, 0xf40f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_mjlstory[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf200, 0xf3ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf700, 0xf70f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_mjlstory[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf200, 0xf3ff, pstadium_palette_w ),
		new Memory_WriteAddress( 0xf700, 0xf70f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_galkoku[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf00f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf400, 0xf5ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_galkoku[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf00f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf400, 0xf5ff, galkoku_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_galkaika[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf00f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf400, 0xf5ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_galkaika[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf00f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf400, 0xf5ff, galkaika_palette_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM, nb1413m3_nvram, nb1413m3_nvram_size ),	// tokimbsj
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_tokyogal[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf400, 0xf40f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_tokyogal[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, galkaika_palette_w ),
		new Memory_WriteAddress( 0xf400, 0xf40f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress readmem_av2mj1[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xefff, MRA_ROM ),
		new Memory_ReadAddress( 0xf000, 0xf1ff, pstadium_palette_r ),
		new Memory_ReadAddress( 0xf500, 0xf50f, pstadium_paltbl_r ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem_av2mj1[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xefff, MWA_ROM ),
		new Memory_WriteAddress( 0xf000, 0xf1ff, pstadium_palette_w ),
		new Memory_WriteAddress( 0xf500, 0xf50f, pstadium_paltbl_w ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static ReadHandlerPtr io_pstadium_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x9000:	return nb1413m3_inputport0_r();
			case	0xa000:	return nb1413m3_inputport1_r();
			case	0xb000:	return nb1413m3_inputport2_r();
			case	0xc000:	return nb1413m3_inputport3_r();
			case	0xf000:	return nb1413m3_dipsw1_r();
			case	0xf800:	return nb1413m3_dipsw2_r();
			default:	return 0xff;
		}
	} };
	
	public static IO_ReadPort readport_pstadium[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0xffff, io_pstadium_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_pstadium_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x0000:	pstadium_radrx_w(data); break;
			case	0x0100:	pstadium_radry_w(data); break;
			case	0x0200:	break;
			case	0x0300:	break;
			case	0x0400:	pstadium_sizex_w(data); break;
			case	0x0500:	pstadium_sizey_w(data); break;
			case	0x0600:	pstadium_dispflag_w(data); break;
			case	0x0700:	break;
			case	0x1000:	pstadium_drawx_w(data); break;
			case	0x2000:	pstadium_drawy_w(data); break;
			case	0x3000:	pstadium_scrollx_w(data); break;
			case	0x4000:	pstadium_scrolly_w(data); break;
			case	0x5000:	pstadium_gfxflag_w(data); break;
			case	0x6000:	pstadium_romsel_w(data); break;
			case	0x7000:	pstadium_paltblnum_w(data); break;
			case	0x8000:	pstadium_sound_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(data); break;
			case	0xb000:	nb1413m3_vcrctrl_w(data); break;
			case	0xd000:	break;
			case	0xf000:	nb1413m3_outcoin_w(data); break;
		}
	} };
	
	public static IO_WritePort writeport_pstadium[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_pstadium_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr io_galkoku_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		if (offset < 0x8000) return nb1413m3_sndrom_r(offset);
	
		switch (offset & 0xff00)
		{
			case	0x9000:	return nb1413m3_inputport0_r();
			case	0xa000:	return nb1413m3_inputport1_r();
			case	0xb000:	return nb1413m3_inputport2_r();
			case	0xc000:	return nb1413m3_inputport3_r();
			case	0xf000:	return nb1413m3_dipsw1_r();
			case	0xf100:	return nb1413m3_dipsw2_r();
			default:	return 0xff;
		}
	} };
	
	public static IO_ReadPort readport_galkoku[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x0000, 0xffff, io_galkoku_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr io_galkoku_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		offset = (((offset & 0xff00) >> 8) | ((offset & 0x00ff) << 8));
	
		switch (offset & 0xff00)
		{
			case	0x0000:	pstadium_radrx_w(data); break;
			case	0x0100:	pstadium_radry_w(data); break;
			case	0x0200:	break;
			case	0x0300:	break;
			case	0x0400:	pstadium_sizex_w(data); break;
			case	0x0500:	pstadium_sizey_w(data); break;
			case	0x0600:	pstadium_dispflag_w(data); break;
			case	0x0700:	break;
			case	0x1000:	pstadium_drawx_w(data); break;
			case	0x2000:	pstadium_drawy_w(data); break;
			case	0x3000:	pstadium_scrollx_w(data); break;
			case	0x4000:	pstadium_scrolly_w(data); break;
			case	0x5000:	pstadium_gfxflag_w(data); break;
			case	0x6000:	pstadium_romsel_w(data); break;
			case	0x7000:	pstadium_paltblnum_w(data); break;
			case	0x8000:	YM3812_control_port_0_w(0, data); break;
			case	0x8100:	YM3812_write_port_0_w(0, data); break;
			case	0xa000:	nb1413m3_inputportsel_w(data); break;
			case	0xb000:	nb1413m3_sndrombank1_w(data); break;
			case	0xc000:	nb1413m3_nmi_clock_w(data); break;
			case	0xd000:	DAC_0_signed_data_w(0, data); break;
			case	0xe000:	break;
			case	0xf000:	nb1413m3_outcoin_w(data); break;
		}
	} };
	
	public static IO_WritePort writeport_galkoku[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0000, 0xffff, io_galkoku_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress sound_readmem_pstadium[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x7fff, MRA_RAM ),
		new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK1 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem_pstadium[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x7fff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static IO_ReadPort sound_readport_pstadium[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x00, pstadium_sound_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport_pstadium[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x00, DAC_0_signed_data_w ),
		new IO_WritePort( 0x02, 0x02, DAC_1_signed_data_w ),
		new IO_WritePort( 0x04, 0x04, pstadium_soundbank_w ),
		new IO_WritePort( 0x06, 0x06, IOWP_NOP ),
		new IO_WritePort( 0x80, 0x80, YM3812_control_port_0_w ),
		new IO_WritePort( 0x81, 0x81, YM3812_write_port_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	static InputPortPtr input_ports_pstadium = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1 (Easy); )
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4 (Hard); )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_triplew1 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1 (Easy); )
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4 (Hard); )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_ntopstar = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1 (Easy); )
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4 (Hard); )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mjlstory = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1 (Easy); )
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4 (Hard); )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_vanilla = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1 (Easy); )
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4 (Hard); )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_qmhayaku = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x03, "1 (Easy); )
		PORT_DIPSETTING(    0x02, "2" );
		PORT_DIPSETTING(    0x01, "3" );
		PORT_DIPSETTING(    0x00, "4 (Hard); )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x04, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Game Sounds" );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Character Display Test" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_galkoku = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy);
		PORT_DIPSETTING(    0x06, "2");
		PORT_DIPSETTING(    0x05, "3");
		PORT_DIPSETTING(    0x04, "4");
		PORT_DIPSETTING(    0x03, "5");
		PORT_DIPSETTING(    0x02, "6");
		PORT_DIPSETTING(    0x01, "7");
		PORT_DIPSETTING(    0x00, "8 (Hard);
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, "Character Display Test" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_BIT( 0xff, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_galkaika = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Character Display Test" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "Debug Mode" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_tokyogal = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Character Display Test" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_tokimbsj = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 1-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 1-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Character Display Test" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_mcontest = new InputPortPtr(){ public void handler() { 
	
		// I don't have manual for this game.
	
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 1-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 1-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 1-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 1-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "Character Display Test" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 1-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 1-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x01, 0x01, "DIPSW 2-1" );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "DIPSW 2-2" );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, "DIPSW 2-3" );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x08, "DIPSW 2-4" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, "DIPSW 2-5" );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "DIPSW 2-6" );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "DIPSW 2-7" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, "DIPSW 2-8" );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_SERVICE1 );	// SERVICE
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_av2mj1 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* (0) DIPSW-A */
		PORT_DIPNAME( 0x07, 0x07, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x07, "1 (Easy);
		PORT_DIPSETTING(    0x06, "2");
		PORT_DIPSETTING(    0x05, "3");
		PORT_DIPSETTING(    0x04, "4");
		PORT_DIPSETTING(    0x03, "5");
		PORT_DIPSETTING(    0x02, "6");
		PORT_DIPSETTING(    0x01, "7");
		PORT_DIPSETTING(    0x00, "8 (Hard);
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Coinage") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_2C") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_DIPNAME( 0xc0, 0xc0, "Video Playback Time" );
		PORT_DIPSETTING(    0xc0, "Type-A");
		PORT_DIPSETTING(    0x80, "Type-B");
		PORT_DIPSETTING(    0x40, "Type-C");
		PORT_DIPSETTING(    0x00, "Type-D");
	
		PORT_START(); 	/* (1) DIPSW-B */
		PORT_DIPNAME( 0x03, 0x03, "Attract mode" );
		PORT_DIPSETTING(    0x03, "No attract mode");
		PORT_DIPSETTING(    0x02, "Once per 10min.");
		PORT_DIPSETTING(    0x01, "Once per 5min.");
		PORT_DIPSETTING(    0x00, "Normal");
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_DIPNAME( 0x08, 0x08, "Graphic ROM Test" );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 	/* (2) PORT 0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED );	// BUSY FLAG ?
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNUSED );	//
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE3 );	// MEMORY RESET
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_SERVICE2 );	// ANALYZER
		PORT_SERVICE( 0x10, IP_ACTIVE_LOW );		// TEST
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_COIN1 );	// COIN1
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START3 );	// CREDIT CLEAR
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );	// COIN2
	
		NBMJCTRL_PORT1	/* (3) PORT 1-1 */
		NBMJCTRL_PORT2	/* (4) PORT 1-2 */
		NBMJCTRL_PORT3	/* (5) PORT 1-3 */
		NBMJCTRL_PORT4	/* (6) PORT 1-4 */
		NBMJCTRL_PORT5	/* (7) PORT 1-5 */
	INPUT_PORTS_END(); }}; 
	
	
	static YM3812interface pstadium_ym3812_interface = new YM3812interface
	(
		1,				/* 1 chip */
		25000000/6.25,			/* 4.00 MHz */
		new int[] { 35 }
	);
	
	static YM3812interface galkoku_ym3812_interface = new YM3812interface
	(
		1,				/* 1 chip */
		25000000/10,			/* 2.50 Mhz */
		new int[] { 50 }
	);
	
	static DACinterface pstadium_dac_interface = new DACinterface
	(
		2,				/* 2 channels */
		new int[] { 50, 50 },
	);
	
	static DACinterface galkoku_dac_interface = new DACinterface
	(
		1,				/* 1 channel */
		new int[] { 50 },
	);
	
	
	#define NBMJDRV1(_name_, _mrmem_, _mwmem_, _mrport_, _mwport_, _nvram_) \
	static MachineDriver machine_driver_##_name_ = new MachineDriver\
	( \
		new MachineCPU[] { \
			new MachineCPU( \
				CPU_Z80, \
				6000000/2,		/* 3.00 Mhz */ \
				readmem_##_mrmem_, writemem_##_mwmem_, readport_##_mrport_, writeport_##_mwport_, \
				nb1413m3_interrupt, 1 \
			), \
			new MachineCPU( \
				CPU_Z80 | CPU_AUDIO_CPU, \
			/*	4000000,	*/	/* 4.00 Mhz */ \
				3900000,		/* 4.00 Mhz */ \
				sound_readmem_pstadium, sound_writemem_pstadium, sound_readport_pstadium, sound_writeport_pstadium, \
				interrupt, 128 \
			) \
		}, \
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION, \
		1, \
		nb1413m3_init_machine, \
	\
		/* video hardware */ \
		1024, 512, new rectangle( 0, 640-1, 255, 495-1 ), \
		null, \
		256, 256*4, \
		null, \
	\
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_PIXEL_ASPECT_RATIO_1_2, \
		null, \
		pstadium_vh_start, \
		pstadium_vh_stop, \
		pstadium_vh_screenrefresh, \
	\
		/* sound hardware */ \
		0, 0, 0, 0, \
		new MachineSound[] { \
			new MachineSound( \
				SOUND_YM3812, \
				pstadium_ym3812_interface \
			), \
			new MachineSound( \
				SOUND_DAC, \
				pstadium_dac_interface \
			) \
		}, \
		##_nvram_ \
	);
	
	#define NBMJDRV2(_name_, _mrmem_, _mwmem_, _mrport_, _mwport_, _nvram_) \
	static MachineDriver machine_driver_##_name_ = new MachineDriver\
	( \
		new MachineCPU[] { \
			new MachineCPU( \
				CPU_Z80 | CPU_16BIT_PORT, \
				25000000/6.25,		/* 4.00 Mhz ? */ \
				readmem_##_mrmem_, writemem_##_mwmem_, readport_##_mrport_, writeport_##_mwport_, \
				nb1413m3_interrupt, 128 \
			) \
		}, \
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION, \
		1, \
		nb1413m3_init_machine, \
	\
		/* video hardware */ \
		1024, 512, new rectangle( 0, 640-1, 255, 495-1 ), \
		null, \
		256, 256*4, \
		null, \
	\
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_PIXEL_ASPECT_RATIO_1_2, \
		null, \
		pstadium_vh_start, \
		pstadium_vh_stop, \
		galkoku_vh_screenrefresh, \
	\
		/* sound hardware */ \
		0, 0, 0, 0, \
		new MachineSound[] { \
			new MachineSound( \
				SOUND_YM3812, \
				galkoku_ym3812_interface \
			), \
			new MachineSound( \
				SOUND_DAC, \
				galkoku_dac_interface \
			) \
		}, \
		##_nvram_ \
	);
	
	
	//	     NAME,  MAIN_RM,  MAIN_WM,  MAIN_RP,  MAIN_WP, NV_RAM
	NBMJDRV1(pstadium, pstadium, pstadium, pstadium, pstadium, 0)
	NBMJDRV1(triplew1, triplew1, triplew1, pstadium, pstadium, 0)
	NBMJDRV1(triplew2, triplew2, triplew2, pstadium, pstadium, 0)
	NBMJDRV1(ntopstar, pstadium, pstadium, pstadium, pstadium, 0)
	NBMJDRV1(mjlstory, mjlstory, mjlstory, pstadium, pstadium, 0)
	NBMJDRV1( vanilla, pstadium, pstadium, pstadium, pstadium, 0)
	NBMJDRV1(qmhayaku, pstadium, pstadium, pstadium, pstadium, 0)
	NBMJDRV2( galkoku,  galkoku,  galkoku,  galkoku,  galkoku, 0)
	NBMJDRV2(galkaika, galkaika, galkaika,  galkoku,  galkoku, 0)
	NBMJDRV2(tokyogal, tokyogal, tokyogal,  galkoku,  galkoku, 0)
	NBMJDRV2(tokimbsj, galkaika, galkaika,  galkoku,  galkoku, nb1413m3_nvram_handler)
	NBMJDRV2(mcontest,  galkoku,  galkoku,  galkoku,  galkoku, 0)
	NBMJDRV1(  av2mj1,   av2mj1,   av2mj1, pstadium, pstadium, 0)
	
	
	static RomLoadPtr rom_pstadium = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "psdm_01.bin",  0x00000,  0x10000, 0x4af81589 );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "psdm_03.bin",  0x00000,  0x10000, 0xac17cef2 );
		ROM_LOAD( "psdm_02.bin",  0x10000,  0x10000, 0xefefe881 );
	
		ROM_REGION( 0x110000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "psdm_04.bin",  0x000000, 0x10000, 0x01957a76 );
		ROM_LOAD( "psdm_05.bin",  0x010000, 0x10000, 0xf5dc1d20 );
		ROM_LOAD( "psdm_06.bin",  0x020000, 0x10000, 0x6fc89b50 );
		ROM_LOAD( "psdm_07.bin",  0x030000, 0x10000, 0xaec64ff4 );
		ROM_LOAD( "psdm_08.bin",  0x040000, 0x10000, 0xebeaf64a );
		ROM_LOAD( "psdm_09.bin",  0x050000, 0x10000, 0x854b2914 );
		ROM_LOAD( "psdm_10.bin",  0x060000, 0x10000, 0xeca5cd5a );
		ROM_LOAD( "psdm_11.bin",  0x070000, 0x10000, 0xa2de166d );
		ROM_LOAD( "psdm_12.bin",  0x080000, 0x10000, 0x2c99ec4d );
		ROM_LOAD( "psdm_13.bin",  0x090000, 0x10000, 0x77b99a6e );
		ROM_LOAD( "psdm_14.bin",  0x0a0000, 0x10000, 0xa3cf907b );
		ROM_LOAD( "psdm_15.bin",  0x0b0000, 0x10000, 0xb0da8d18 );
		ROM_LOAD( "psdm_16.bin",  0x0c0000, 0x10000, 0x9a2fd9c5 );
		ROM_LOAD( "psdm_17.bin",  0x0d0000, 0x10000, 0xe462d507 );
		ROM_LOAD( "psdm_18.bin",  0x0e0000, 0x10000, 0xe9ce8e02 );
		ROM_LOAD( "psdm_19.bin",  0x0f0000, 0x10000, 0xf23496c6 );
		ROM_LOAD( "psdm_20.bin",  0x100000, 0x10000, 0xc410ce4b );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_triplew1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "tpw1_01.bin",  0x00000,  0x10000, 0x2542958a );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "tpw1_03.bin",  0x00000,  0x10000, 0xd86cc7d2 );
		ROM_LOAD( "tpw1_02.bin",  0x10000,  0x10000, 0x857656a7 );
	
		ROM_REGION( 0x160000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "tpw1_04.bin",  0x000000, 0x20000, 0xca26ccb3 );
		ROM_LOAD( "tpw1_05.bin",  0x020000, 0x20000, 0x26501af0 );
		ROM_LOAD( "tpw1_06.bin",  0x040000, 0x10000, 0x789bbacd );
		ROM_LOAD( "tpw1_07.bin",  0x050000, 0x10000, 0x38aaad61 );
		ROM_LOAD( "tpw1_08.bin",  0x060000, 0x10000, 0x9f4042b4 );
		ROM_LOAD( "tpw1_09.bin",  0x070000, 0x10000, 0x388a78b9 );
		ROM_LOAD( "tpw1_10.bin",  0x080000, 0x10000, 0x7a19730d );
		ROM_LOAD( "tpw1_11.bin",  0x090000, 0x10000, 0x1239a0c6 );
		ROM_LOAD( "tpw1_12.bin",  0x0a0000, 0x10000, 0xca469c52 );
		ROM_LOAD( "tpw1_13.bin",  0x0b0000, 0x10000, 0x0ca520c0 );
		ROM_LOAD( "tpw1_14.bin",  0x0c0000, 0x10000, 0x3880db99 );
		ROM_LOAD( "tpw1_15.bin",  0x0d0000, 0x10000, 0x996ea3e8 );
		ROM_LOAD( "tpw1_16.bin",  0x0e0000, 0x10000, 0x415ae47c );
		ROM_LOAD( "tpw1_17.bin",  0x0f0000, 0x10000, 0xb5c88f0e );
		ROM_LOAD( "tpw1_18.bin",  0x100000, 0x10000, 0xdef06191 );
		ROM_LOAD( "tpw1_19.bin",  0x110000, 0x10000, 0xb293561b );
		ROM_LOAD( "tpw1_20.bin",  0x120000, 0x10000, 0x81bfa331 );
		ROM_LOAD( "tpw1_21.bin",  0x130000, 0x10000, 0x2dbb68e5 );
		ROM_LOAD( "tpw1_22.bin",  0x140000, 0x10000, 0x9633278c );
		ROM_LOAD( "tpw1_23.bin",  0x150000, 0x10000, 0x11580513 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_triplew2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "tpw2_01.bin",  0x00000,  0x10000, 0x2637f19d );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "tpw2_03.bin",  0x00000,  0x10000, 0x8e7922c3 );
		ROM_LOAD( "tpw2_02.bin",  0x10000,  0x10000, 0x5339692d );
	
		ROM_REGION( 0x200000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "tpw2_04.bin",  0x000000, 0x20000, 0xd4af2c04 );
		ROM_LOAD( "tpw2_05.bin",  0x020000, 0x20000, 0xfff198c8 );
		ROM_LOAD( "tpw2_06.bin",  0x040000, 0x20000, 0x4966b15b );
		ROM_LOAD( "tpw2_07.bin",  0x060000, 0x20000, 0xde1b8788 );
		ROM_LOAD( "tpw2_08.bin",  0x080000, 0x20000, 0xfb1b1ebc );
		ROM_LOAD( "tpw2_09.bin",  0x0a0000, 0x10000, 0xd40cacfd );
		ROM_LOAD( "tpw2_10.bin",  0x0b0000, 0x10000, 0x8fa96a92 );
		ROM_LOAD( "tpw2_11.bin",  0x0c0000, 0x10000, 0xa6a44edd );
		ROM_LOAD( "tpw2_12.bin",  0x0d0000, 0x10000, 0xd01a3a6a );
		ROM_LOAD( "tpw2_13.bin",  0x0e0000, 0x10000, 0x6b4ebd1f );
		ROM_LOAD( "tpw2_14.bin",  0x0f0000, 0x10000, 0x383d2735 );
		ROM_LOAD( "tpw2_15.bin",  0x100000, 0x10000, 0x682110f5 );
		ROM_LOAD( "tpw2_16.bin",  0x110000, 0x10000, 0x466eea24 );
		ROM_LOAD( "tpw2_17.bin",  0x120000, 0x10000, 0xa422ece3 );
		ROM_LOAD( "tpw2_18.bin",  0x130000, 0x10000, 0xf65b699d );
		ROM_LOAD( "tpw2_19.bin",  0x140000, 0x10000, 0x8356beac );
		ROM_LOAD( "tpw2_20.bin",  0x150000, 0x10000, 0x240c408e );
		ROM_LOAD( "mj_1802.bin",  0x180000, 0x80000, 0xe6213f10 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_ntopstar = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "ntsr_01.bin",  0x00000,  0x10000, 0x3a4325f2 );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "ntsr_03.bin",  0x00000,  0x10000, 0x747ba06a );
		ROM_LOAD( "ntsr_02.bin",  0x10000,  0x10000, 0x12334718 );
	
		ROM_REGION( 0x140000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "ntsr_04.bin",  0x000000, 0x20000, 0x06edf3a4 );
		ROM_LOAD( "ntsr_05.bin",  0x020000, 0x20000, 0xb3f014fa );
		ROM_LOAD( "ntsr_06.bin",  0x040000, 0x10000, 0x9333ebcb );
		ROM_LOAD( "ntsr_07.bin",  0x050000, 0x10000, 0x0948f999 );
		ROM_LOAD( "ntsr_08.bin",  0x060000, 0x10000, 0xabbd7494 );
		ROM_LOAD( "ntsr_09.bin",  0x070000, 0x10000, 0xdd84badd );
		ROM_LOAD( "ntsr_10.bin",  0x080000, 0x10000, 0x7083a505 );
		ROM_LOAD( "ntsr_11.bin",  0x090000, 0x10000, 0x45ed0f6d );
		ROM_LOAD( "ntsr_12.bin",  0x0a0000, 0x10000, 0x3d51ae82 );
		ROM_LOAD( "ntsr_13.bin",  0x0b0000, 0x10000, 0xeccde427 );
		ROM_LOAD( "ntsr_14.bin",  0x0c0000, 0x10000, 0xdd21bbfb );
		ROM_LOAD( "ntsr_15.bin",  0x0d0000, 0x10000, 0x5556024b );
		ROM_LOAD( "ntsr_16.bin",  0x0e0000, 0x10000, 0xf1273c7f );
		ROM_LOAD( "ntsr_17.bin",  0x0f0000, 0x10000, 0xd5574307 );
		ROM_LOAD( "ntsr_18.bin",  0x100000, 0x10000, 0x71566140 );
		ROM_LOAD( "ntsr_19.bin",  0x110000, 0x10000, 0x6c880b9d );
		ROM_LOAD( "ntsr_20.bin",  0x120000, 0x10000, 0x4b832d37 );
		ROM_LOAD( "ntsr_21.bin",  0x130000, 0x10000, 0x133183db );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mjlstory = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "mjls_01.bin",  0x00000,  0x10000, 0xa9febe8b );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "mjls_03.bin",  0x00000,  0x10000, 0x15e54af0 );
		ROM_LOAD( "mjls_02.bin",  0x10000,  0x10000, 0xda976e4f );
	
		ROM_REGION( 0x190000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "mjls_04.bin",  0x000000, 0x20000, 0xd3e642ee );
		ROM_LOAD( "mjls_05.bin",  0x020000, 0x20000, 0xdc888639 );
		ROM_LOAD( "mjls_06.bin",  0x040000, 0x20000, 0x8a191142 );
		ROM_LOAD( "mjls_07.bin",  0x060000, 0x20000, 0x384b9c40 );
		ROM_LOAD( "mjls_08.bin",  0x080000, 0x20000, 0x072ac9b6 );
		ROM_LOAD( "mjls_09.bin",  0x0a0000, 0x20000, 0xf4dc5e77 );
		ROM_LOAD( "mjls_10.bin",  0x0c0000, 0x20000, 0xaa5a165a );
		ROM_LOAD( "mjls_11.bin",  0x0e0000, 0x20000, 0x25a44a56 );
		ROM_LOAD( "mjls_12.bin",  0x100000, 0x20000, 0x2e19183c );
		ROM_LOAD( "mjls_13.bin",  0x120000, 0x20000, 0xcc08652c );
		ROM_LOAD( "mjls_14.bin",  0x140000, 0x20000, 0xf469f3a5 );
		ROM_LOAD( "mjls_15.bin",  0x160000, 0x20000, 0x815b187a );
		ROM_LOAD( "mjls_16.bin",  0x180000, 0x10000, 0x53366690 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_vanilla = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "vanilla.01",   0x00000,  0x10000, 0x2a3341a8 );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "vanilla.03",   0x00000,  0x10000, 0xe035842f );
		ROM_LOAD( "vanilla.02",   0x10000,  0x10000, 0x93d8398a );
	
		ROM_REGION( 0x200000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "vanilla.04",   0x000000, 0x20000, 0xf21e1ff4 );
		ROM_LOAD( "vanilla.05",   0x020000, 0x20000, 0x15d6ff78 );
		ROM_LOAD( "vanilla.06",   0x040000, 0x20000, 0x90da7b35 );
		ROM_LOAD( "vanilla.07",   0x060000, 0x20000, 0x71b2896f );
		ROM_LOAD( "vanilla.08",   0x080000, 0x20000, 0xdd195233 );
		ROM_LOAD( "vanilla.09",   0x0a0000, 0x20000, 0x5521c7a1 );
		ROM_LOAD( "vanilla.10",   0x0c0000, 0x20000, 0xe7d781da );
		ROM_LOAD( "vanilla.11",   0x0e0000, 0x20000, 0xba7fbf3d );
		ROM_LOAD( "vanilla.12",   0x100000, 0x20000, 0x56fe9708 );
		ROM_LOAD( "vanilla.13",   0x120000, 0x20000, 0x91011a9e );
		ROM_LOAD( "vanilla.14",   0x140000, 0x20000, 0x460db736 );
		ROM_LOAD( "vanilla.15",   0x160000, 0x20000, 0xf977655c );
		ROM_LOAD( "vanilla.16",   0x180000, 0x10000, 0xf286a9db );
		ROM_LOAD( "vanilla.17",   0x190000, 0x10000, 0x9b0a7bb5 );
		ROM_LOAD( "vanilla.18",   0x1a0000, 0x10000, 0x54120c24 );
		ROM_LOAD( "vanilla.19",   0x1b0000, 0x10000, 0xc1bb8643 );
		ROM_LOAD( "vanilla.20",   0x1c0000, 0x10000, 0x26bb26a0 );
		ROM_LOAD( "vanilla.21",   0x1d0000, 0x10000, 0x61046b51 );
		ROM_LOAD( "vanilla.22",   0x1e0000, 0x10000, 0x66de02e6 );
		ROM_LOAD( "vanilla.23",   0x1f0000, 0x10000, 0x64186e8a );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_qmhayaku = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* main program */
		ROM_LOAD( "1.4e",    0x00000,  0x10000, 0x5a73cdf8 );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "3.4t",    0x00000,  0x10000, 0xd420dac8 );
		ROM_LOAD( "2.4s",    0x10000,  0x10000, 0xf88cb623 );
	
		ROM_REGION( 0x200000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "4.9b",    0x000000, 0x20000, 0x2fba26fe );
		ROM_LOAD( "5.9d",    0x020000, 0x20000, 0x105f9930 );
		ROM_LOAD( "6.9e",    0x040000, 0x20000, 0x5e8f0177 );
		ROM_LOAD( "7.9f",    0x060000, 0x20000, 0x612803ba );
		ROM_LOAD( "8.9j",    0x080000, 0x20000, 0x874fe074 );
		ROM_LOAD( "9.9k",    0x0a0000, 0x20000, 0xafa873d2 );
		ROM_LOAD( "10.9l",   0x0c0000, 0x20000, 0x17a4a609 );
		ROM_LOAD( "11.9n",   0x0e0000, 0x20000, 0xd2357c72 );
		ROM_LOAD( "12.9p",   0x100000, 0x20000, 0x4b63c040 );
		ROM_LOAD( "13.7a",   0x120000, 0x20000, 0xa182d9cd );
		ROM_LOAD( "14.7b",   0x140000, 0x20000, 0x22b1f1fd );
		ROM_LOAD( "15.7d",   0x160000, 0x20000, 0x3db4df6c );
		ROM_LOAD( "16.7e",   0x180000, 0x20000, 0xc1283063 );
		ROM_LOAD( "17.7f",   0x1a0000, 0x10000, 0x4ca71ef1 );
		ROM_LOAD( "18.7j",   0x1b0000, 0x10000, 0x81190d74 );
		ROM_LOAD( "19.7k",   0x1c0000, 0x10000, 0xcad37c2f );
		ROM_LOAD( "20.7l",   0x1d0000, 0x10000, 0x18e18174 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galkoku = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* program */
		ROM_LOAD( "gkok_01.bin",  0x00000,  0x10000, 0x254c526c );
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* voice */
		ROM_LOAD( "gkok_02.bin",  0x00000,  0x10000, 0x3dec7469 );
		ROM_LOAD( "gkok_03.bin",  0x10000,  0x10000, 0x66f51b21 );
	
		ROM_REGION( 0x110000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "gkok_04.bin",  0x000000, 0x10000, 0x741815a5 );
		ROM_LOAD( "gkok_05.bin",  0x010000, 0x10000, 0x28a17cd8 );
		ROM_LOAD( "gkok_06.bin",  0x020000, 0x10000, 0x8eac2143 );
		ROM_LOAD( "gkok_07.bin",  0x030000, 0x10000, 0xde5f3f20 );
		ROM_LOAD( "gkok_08.bin",  0x040000, 0x10000, 0xf3348126 );
		ROM_LOAD( "gkok_09.bin",  0x050000, 0x10000, 0x691f2521 );
		ROM_LOAD( "gkok_10.bin",  0x060000, 0x10000, 0xf1b0b411 );
		ROM_LOAD( "gkok_11.bin",  0x070000, 0x10000, 0xef42af9e );
		ROM_LOAD( "gkok_12.bin",  0x080000, 0x10000, 0xe2b32195 );
		ROM_LOAD( "gkok_13.bin",  0x090000, 0x10000, 0x83d913a1 );
		ROM_LOAD( "gkok_14.bin",  0x0a0000, 0x10000, 0x04c97de9 );
		ROM_LOAD( "gkok_15.bin",  0x0b0000, 0x10000, 0x3845280d );
		ROM_LOAD( "gkok_16.bin",  0x0c0000, 0x10000, 0x7472a7ce );
		ROM_LOAD( "gkok_17.bin",  0x0d0000, 0x10000, 0x92b605a2 );
		ROM_LOAD( "gkok_18.bin",  0x0e0000, 0x10000, 0x8bb7bdcc );
		ROM_LOAD( "gkok_19.bin",  0x0f0000, 0x10000, 0xb1b4643a );
		ROM_LOAD( "gkok_20.bin",  0x100000, 0x10000, 0x36107e6f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_galkaika = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* program */
		ROM_LOAD( "gkai_01.bin",  0x00000,  0x10000, 0x81b89559 );
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* voice */
		ROM_LOAD( "gkai_02.bin",  0x00000,  0x10000, 0xdb899dd5 );
		ROM_LOAD( "gkai_03.bin",  0x10000,  0x10000, 0xa66a1c52 );
	
		ROM_REGION( 0x120000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "gkai_04.bin",  0x000000, 0x20000, 0xb1071e49 );
		ROM_LOAD( "gkai_05.bin",  0x020000, 0x20000, 0xe5162326 );
		ROM_LOAD( "gkai_06.bin",  0x040000, 0x20000, 0xe0cebb15 );
		ROM_LOAD( "gkai_07.bin",  0x060000, 0x20000, 0x26915aa7 );
		ROM_LOAD( "gkai_08.bin",  0x080000, 0x20000, 0xdf009be3 );
		ROM_LOAD( "gkai_09.bin",  0x0a0000, 0x20000, 0xcebfb4f3 );
		ROM_LOAD( "gkai_10.bin",  0x0c0000, 0x20000, 0x43ecb3c5 );
		ROM_LOAD( "gkai_11.bin",  0x0e0000, 0x20000, 0x66f4dbfa );
		ROM_LOAD( "gkai_12.bin",  0x100000, 0x10000, 0xdc35168a );
		ROM_LOAD( "gkai_13.bin",  0x110000, 0x10000, 0xd9f495f3 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_tokyogal = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* program */
		ROM_LOAD( "tgal_21.bin",  0x00000,  0x10000, 0xad4eecec );
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* voice */
		ROM_LOAD( "tgal_22.bin",  0x00000,  0x10000, 0x36be0868 );
	
		ROM_REGION( 0x140000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "tgal_01.bin",  0x000000, 0x10000, 0x6a7a5c13 );
		ROM_LOAD( "tgal_02.bin",  0x010000, 0x10000, 0x31e052e6 );
		ROM_LOAD( "tgal_03.bin",  0x020000, 0x10000, 0xd4bbf1e6 );
		ROM_LOAD( "tgal_04.bin",  0x030000, 0x10000, 0xf2b30256 );
		ROM_LOAD( "tgal_05.bin",  0x040000, 0x10000, 0xaf820677 );
		ROM_LOAD( "tgal_06.bin",  0x050000, 0x10000, 0xd9ff9b76 );
		ROM_LOAD( "tgal_07.bin",  0x060000, 0x10000, 0xd5288e37 );
		ROM_LOAD( "tgal_08.bin",  0x070000, 0x10000, 0x824fa5cc );
		ROM_LOAD( "tgal_09.bin",  0x080000, 0x10000, 0x795b8f8c );
		ROM_LOAD( "tgal_10.bin",  0x090000, 0x10000, 0xf2c13f7a );
		ROM_LOAD( "tgal_11.bin",  0x0a0000, 0x10000, 0x551f6fb4 );
		ROM_LOAD( "tgal_12.bin",  0x0b0000, 0x10000, 0x78db30a7 );
		ROM_LOAD( "tgal_13.bin",  0x0c0000, 0x10000, 0x04a81e7a );
		ROM_LOAD( "tgal_14.bin",  0x0d0000, 0x10000, 0x12b43b21 );
		ROM_LOAD( "tgal_15.bin",  0x0e0000, 0x10000, 0xaf06f649 );
		ROM_LOAD( "tgal_16.bin",  0x0f0000, 0x10000, 0x2996431a );
		ROM_LOAD( "tgal_17.bin",  0x100000, 0x10000, 0x470dde3c );
		ROM_LOAD( "tgal_18.bin",  0x110000, 0x10000, 0x0d04d3bc );
		ROM_LOAD( "tgal_19.bin",  0x120000, 0x10000, 0x1c8fe0e8 );
		ROM_LOAD( "tgal_20.bin",  0x130000, 0x10000, 0xb8542eeb );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_tokimbsj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* program */
		ROM_LOAD( "tmbj_01.bin",  0x00000,  0x10000, 0xb335c300 );
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* voice */
		ROM_LOAD( "tmbj_02.bin",  0x00000,  0x10000, 0x36be0868 );
	
		ROM_REGION( 0x140000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "tmbj_03.bin",  0x000000, 0x10000, 0x6a7a5c13 );
		ROM_LOAD( "tmbj_04.bin",  0x010000, 0x10000, 0x09e3f23d );
		ROM_LOAD( "tmbj_05.bin",  0x020000, 0x10000, 0xd4bbf1e6 );
		ROM_LOAD( "tmbj_06.bin",  0x030000, 0x10000, 0xf2b30256 );
		ROM_LOAD( "tmbj_07.bin",  0x040000, 0x10000, 0xaf820677 );
		ROM_LOAD( "tmbj_08.bin",  0x050000, 0x10000, 0xd9ff9b76 );
		ROM_LOAD( "tmbj_09.bin",  0x060000, 0x10000, 0xd5288e37 );
		ROM_LOAD( "tmbj_10.bin",  0x070000, 0x10000, 0x824fa5cc );
		ROM_LOAD( "tmbj_11.bin",  0x080000, 0x10000, 0x795b8f8c );
		ROM_LOAD( "tmbj_12.bin",  0x090000, 0x10000, 0xf2c13f7a );
		ROM_LOAD( "tmbj_13.bin",  0x0a0000, 0x10000, 0x551f6fb4 );
		ROM_LOAD( "tmbj_14.bin",  0x0b0000, 0x10000, 0x78db30a7 );
		ROM_LOAD( "tmbj_15.bin",  0x0c0000, 0x10000, 0x04a81e7a );
		ROM_LOAD( "tmbj_16.bin",  0x0d0000, 0x10000, 0x12b43b21 );
		ROM_LOAD( "tmbj_17.bin",  0x0e0000, 0x10000, 0xaf06f649 );
		ROM_LOAD( "tmbj_18.bin",  0x0f0000, 0x10000, 0x2996431a );
		ROM_LOAD( "tmbj_19.bin",  0x100000, 0x10000, 0x470dde3c );
		ROM_LOAD( "tmbj_20.bin",  0x110000, 0x10000, 0x0d04d3bc );
		ROM_LOAD( "tmbj_21.bin",  0x120000, 0x10000, 0xb608d6b1 );
		ROM_LOAD( "tmbj_22.bin",  0x130000, 0x10000, 0xe706fc87 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_mcontest = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* program */
		ROM_LOAD( "mcon_01.bin",  0x00000, 0x10000, 0x79a30028 );
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* voice */
		ROM_LOAD( "mcon_02.bin",  0x00000, 0x10000, 0x236b8fdc );
		ROM_LOAD( "mcon_03.bin",  0x10000, 0x10000, 0x6d6bdefb );
	
		ROM_REGION( 0x160000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "mcon_04.bin",  0x000000, 0x20000, 0xadb6e002 );
		ROM_LOAD( "mcon_05.bin",  0x020000, 0x20000, 0xea8ceb49 );
		ROM_LOAD( "mcon_06.bin",  0x040000, 0x10000, 0xd3fee691 );
		ROM_LOAD( "mcon_07.bin",  0x050000, 0x10000, 0x7685a1b1 );
		ROM_LOAD( "mcon_08.bin",  0x060000, 0x10000, 0xeee52454 );
		ROM_LOAD( "mcon_09.bin",  0x070000, 0x10000, 0x2ad2d00f );
		ROM_LOAD( "mcon_10.bin",  0x080000, 0x10000, 0x6ff32ed9 );
		ROM_LOAD( "mcon_11.bin",  0x090000, 0x10000, 0x4f9c340f );
		ROM_LOAD( "mcon_12.bin",  0x0a0000, 0x10000, 0x41cffdf0 );
		ROM_LOAD( "mcon_13.bin",  0x0b0000, 0x10000, 0xd494fdb7 );
		ROM_LOAD( "mcon_14.bin",  0x0c0000, 0x10000, 0x9fe3f75d );
		ROM_LOAD( "mcon_15.bin",  0x0d0000, 0x10000, 0x79fa427a );
		ROM_LOAD( "mcon_16.bin",  0x0e0000, 0x10000, 0xf5ae3668 );
		ROM_LOAD( "mcon_17.bin",  0x0f0000, 0x10000, 0xcb02f51d );
		ROM_LOAD( "mcon_18.bin",  0x100000, 0x10000, 0x8e5fe1bc );
		ROM_LOAD( "mcon_19.bin",  0x110000, 0x10000, 0x5b382cf3 );
		ROM_LOAD( "mcon_20.bin",  0x120000, 0x10000, 0x8ffbd8fe );
		ROM_LOAD( "mcon_21.bin",  0x130000, 0x10000, 0x9476d11d );
		ROM_LOAD( "mcon_22.bin",  0x140000, 0x10000, 0x07d21863 );
		ROM_LOAD( "mcon_23.bin",  0x150000, 0x10000, 0x979e0f93 );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_av2mj1 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1 );/* program */
		ROM_LOAD( "1.bin",       0x00000, 0x10000, 0xdf0f03fb );
	
		ROM_REGION( 0x20000, REGION_CPU2 );/* sub program */
		ROM_LOAD( "3.bin",       0x00000, 0x10000, 0x0cdc9489 );
		ROM_LOAD( "2.bin",       0x10000, 0x10000, 0x6283a444 );
	
		ROM_REGION( 0x200000, REGION_GFX1 );/* gfx */
		ROM_LOAD( "4.bin",       0x000000, 0x20000, 0x18fe29c3 );
		ROM_LOAD( "5.bin",       0x020000, 0x20000, 0x0eff4bbf );
		ROM_LOAD( "6.bin",       0x040000, 0x20000, 0xac351796 );
		ROM_LOAD( "mj-1802.bin", 0x180000, 0x80000, BADCRC( 0xe6213f10 ));
	ROM_END(); }}; 
	
	
	//    YEAR,     NAME,   PARENT,  MACHINE,    INPUT,     INIT, MONITOR, COMPANY, FULLNAME, FLAGS)
	public static GameDriver driver_pstadium	   = new GameDriver("1990"	,"pstadium"	,"pstadium.java"	,rom_pstadium,null	,machine_driver_pstadium	,input_ports_pstadium	,init_pstadium	,ROT0	,	"Nichibutsu", "Mahjong Panic Stadium (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_triplew1	   = new GameDriver("1989"	,"triplew1"	,"pstadium.java"	,rom_triplew1,null	,machine_driver_triplew1	,input_ports_triplew1	,init_triplew1	,ROT0	,	"Nichibutsu", "Mahjong Triple Wars (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_triplew2	   = new GameDriver("1990"	,"triplew2"	,"pstadium.java"	,rom_triplew2,null	,machine_driver_triplew2	,input_ports_triplew1	,init_triplew2	,ROT0	,	"Nichibutsu", "Mahjong Triple Wars 2 (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_ntopstar	   = new GameDriver("1990"	,"ntopstar"	,"pstadium.java"	,rom_ntopstar,null	,machine_driver_ntopstar	,input_ports_ntopstar	,init_ntopstar	,ROT0	,	"Nichibutsu", "Mahjong Nerae! Top Star (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_mjlstory	   = new GameDriver("1991"	,"mjlstory"	,"pstadium.java"	,rom_mjlstory,null	,machine_driver_mjlstory	,input_ports_mjlstory	,init_mjlstory	,ROT0	,	"Nichibutsu", "Mahjong Jikken Love Story (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_vanilla	   = new GameDriver("1991"	,"vanilla"	,"pstadium.java"	,rom_vanilla,null	,machine_driver_vanilla	,input_ports_vanilla	,init_vanilla	,ROT0	,	"Nichibutsu", "Mahjong Vanilla Syndrome (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_qmhayaku	   = new GameDriver("1991"	,"qmhayaku"	,"pstadium.java"	,rom_qmhayaku,null	,machine_driver_qmhayaku	,input_ports_qmhayaku	,init_qmhayaku	,ROT0	,	"Nichibutsu", "Quiz-Mahjong Hayaku Yatteyo! (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_galkoku	   = new GameDriver("1989"	,"galkoku"	,"pstadium.java"	,rom_galkoku,null	,machine_driver_galkoku	,input_ports_galkoku	,init_galkoku	,ROT0	,	"Nichibutsu/T.R.TEC", "Mahjong Gal no Kokuhaku (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_galkaika	   = new GameDriver("1989"	,"galkaika"	,"pstadium.java"	,rom_galkaika,null	,machine_driver_galkaika	,input_ports_galkaika	,init_galkaika	,ROT0	,	"Nichibutsu/T.R.TEC", "Mahjong Gal no Kaika (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_tokyogal	   = new GameDriver("1989"	,"tokyogal"	,"pstadium.java"	,rom_tokyogal,null	,machine_driver_tokyogal	,input_ports_tokyogal	,init_tokyogal	,ROT0	,	"Nichibutsu", "Tokyo Gal Zukan (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_tokimbsj	   = new GameDriver("1989"	,"tokimbsj"	,"pstadium.java"	,rom_tokimbsj,null	,machine_driver_tokimbsj	,input_ports_tokimbsj	,init_tokimbsj	,ROT0	,	"Nichibutsu", "Tokimeki Bishoujo [BET] (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_mcontest	   = new GameDriver("1989"	,"mcontest"	,"pstadium.java"	,rom_mcontest,null	,machine_driver_mcontest	,input_ports_mcontest	,init_mcontest	,ROT0	,	"Nichibutsu", "Miss Mahjong Contest (Japan)", GAME_NO_COCKTAIL)
	public static GameDriver driver_av2mj1	   = new GameDriver("1991"	,"av2mj1"	,"pstadium.java"	,rom_av2mj1,null	,machine_driver_av2mj1	,input_ports_av2mj1	,init_av2mj1	,ROT0	,	"Nichibutsu/Miki Syouji/AV Japan", "AV2Mahjong No.1 Bay Bridge no Seijo", GAME_NO_COCKTAIL | GAME_NOT_WORKING)
}

/***************************************************************************

  Funky Jet                               (c) 1992 Mitchell Corporation

  But actually a Data East game...  Hardware is pretty close to Super Burger
  Time but with a different graphics chip.  And I can't work out the graphics
  format of this chip - it's used in all Data East games from 1991 onwards.
  If you want to help decode it, look at Rohga for an easy example as the fix
  char roms (64k) are in this format.

  The game also uses a protection chip which isn't fully worked out yet.

  Emulation by Bryan McPhail, mish@tendril.co.uk

***************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class funkyjet
{
	
	
	WRITE16_HANDLER( funkyjet_pf2_data16_w );
	WRITE16_HANDLER( funkyjet_pf1_data16_w );
	READ16_HANDLER( funkyjet_pf1_data16_r );
	READ16_HANDLER( funkyjet_pf2_data16_r );
	
	WRITE16_HANDLER( funkyjet_control16_0_w );
	
	extern data16_t *funkyjet_pf1_data16;
	extern data16_t *funkyjet_pf2_data16;
	extern data16_t *funkyjet_pf1_row16;
	extern size_t funkyjet_pf1_data16_size;
	extern size_t funkyjet_pf2_data16_size;
	static data16_t *funkyjet_ram16;
	
	/******************************************************************************/
	
	static data16_t loopback[0x400];
	
	static WRITE16_HANDLER( funkyjet_protection16_w )
	{
		COMBINE_DATA(&loopback[offset]);
	
		if (offset != (0x502 >> 1) &&
			offset != (0x700 >> 1) &&
			offset != (0x70e >> 1) &&
			offset != (0x78e >> 1))
			logerror("CPU #0 PC %06x: warning - write unmapped control address %06x %04x\n",cpu_get_pc(),offset<<1,data);
	
		if (offset == (0x10a >> 1))
		{
			soundlatch_w(0,data&0xff);
			cpu_cause_interrupt(1,H6280_INT_IRQ1);
		}
	
		/*
	
			offset==0x502 - written after joystick read
			offset==0x700 - mirror of flipscreen byte
			offset==0x70e - written after credits read
			offset==0x78e - written after credits read
		*/
	}
	
	static READ16_HANDLER( funkyjet_protection16_r )
	{
	 	switch (offset)
		{
			case 0x148 >> 1: /* EOR mask for joysticks */
				return 0;
			case 0x00c >> 1: /* Player 1 & Player 2 joysticks & fire buttons */
			case 0x24c >> 1:
				return ~(readinputport(0) + (readinputport(1) << 8));
	
			case 0x2d8 >> 1: /* EOR mask for credits */
				return 0;
			case 0x778 >> 1: /* Credits */
				return readinputport(2);
	
			case 0x382 >> 1: /* DIPS */
				return (readinputport(3) + (readinputport(4) << 8));
	
			case 0x56c >> 1:
				return 0;
	
		}
	
		if (offset != (0x778 >> 1))
			logerror("CPU #0 PC %06x: warning - read unmapped control address %06x\n",cpu_get_pc(),offset<<1);
	
	/*
	
	Protection device:
	
		382 is the flag for whether controls are read (bne 0x800)
	
		778 is read and written back to 502
	
	
	*/
	
		return ~0;
	}
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( funkyjet_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0x120000, 0x1207ff, MRA16_RAM },
		{ 0x140000, 0x143fff, MRA16_RAM },
		{ 0x160000, 0x1607ff, MRA16_RAM },
		{ 0x180000, 0x1807ff, funkyjet_protection16_r },
	
		{ 0x320000, 0x321fff, funkyjet_pf1_data16_r },
		{ 0x322000, 0x323fff, funkyjet_pf2_data16_r },
		{ 0x340000, 0x340bff, MRA16_RAM },
		{ 0x342000, 0x342bff, MRA16_RAM }, /* pf2 rowscroll */
	MEMORY_END
	
	static MEMORY_WRITE16_START( funkyjet_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0x120000, 0x1207ff, paletteram16_xxxxBBBBGGGGRRRR_word_w, &paletteram16 },
		{ 0x140000, 0x143fff, MWA16_RAM, &funkyjet_ram16 },
		{ 0x160000, 0x1607ff, MWA16_RAM, &spriteram16 },
		{ 0x180000, 0x1807ff, funkyjet_protection16_w },
	
		{ 0x184000, 0x184001, MWA16_NOP },
		{ 0x188000, 0x188001, MWA16_NOP },
	
		{ 0x300000, 0x30000f, funkyjet_control16_0_w },
		{ 0x320000, 0x321fff, funkyjet_pf1_data16_w, &funkyjet_pf1_data16, &funkyjet_pf1_data16_size },
		{ 0x322000, 0x323fff, funkyjet_pf2_data16_w, &funkyjet_pf2_data16, &funkyjet_pf2_data16_size },
		{ 0x340000, 0x340bff, MWA16_RAM, &funkyjet_pf1_row16 },
		{ 0x342000, 0x342bff, MWA16_RAM }, /* pf2 rowscroll */
	MEMORY_END
	
	/******************************************************************************/
	
	public static WriteHandlerPtr YM2151_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		switch (offset)
		{
		case 0:
			YM2151_register_port_0_w(0,data);
			break;
		case 1:
			YM2151_data_port_0_w(0,data);
			break;
		}
	} };
	
	/* Physical memory map (21 bits) */
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x000000, 0x00ffff, MRA_ROM ),
		new Memory_ReadAddress( 0x100000, 0x100001, MRA_NOP ),
		new Memory_ReadAddress( 0x110000, 0x110001, YM2151_status_port_0_r ),
		new Memory_ReadAddress( 0x120000, 0x120001, OKIM6295_status_0_r ),
		new Memory_ReadAddress( 0x130000, 0x130001, MRA_NOP ), /* This board only has 1 oki chip */
		new Memory_ReadAddress( 0x140000, 0x140001, soundlatch_r ),
		new Memory_ReadAddress( 0x1f0000, 0x1f1fff, MRA_BANK8 ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x000000, 0x00ffff, MWA_ROM ),
		new Memory_WriteAddress( 0x100000, 0x100001, MWA_NOP ), /* YM2203 - this board doesn't have one */
		new Memory_WriteAddress( 0x110000, 0x110001, YM2151_w ),
		new Memory_WriteAddress( 0x120000, 0x120001, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0x130000, 0x130001, MWA_NOP ),
		new Memory_WriteAddress( 0x1f0000, 0x1f1fff, MWA_BANK8 ),
		new Memory_WriteAddress( 0x1fec00, 0x1fec01, H6280_timer_w ),
		new Memory_WriteAddress( 0x1ff402, 0x1ff403, H6280_irq_status_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static InputPortPtr input_ports_funkyjet = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* Player 1 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* button 3 - unused? */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 );
	
		PORT_START(); 	/* Player 2 controls */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );/* button 3 - unused? */
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START2 );
	
		PORT_START(); 	/* Credits */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_COIN3 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_VBLANK );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* Dip switch bank 1 */
	
		/* Dips seem inverted with respect to other Deco games */
	
		/* Some of these coinage options may not be correct.. */
		PORT_DIPNAME( 0xe0, 0xe0, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x40, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0xe0, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0xa0, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x60, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x80, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x20, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x1c, 0x1c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x1c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x18, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x14, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x10, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* Dip switch bank 2 */
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x80, "1" );
		PORT_DIPSETTING(    0x00, "2" );
		PORT_DIPSETTING(    0xc0, "3" );
		PORT_DIPSETTING(    0x40, "4" );
		PORT_DIPNAME( 0x30, 0x30, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x10, "Easy" );
		PORT_DIPSETTING(    0x30, "Normal" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x00, "Hardest" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x04, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x02, DEF_STR( "Yes") );
	  	PORT_DIPNAME( 0x01, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x01, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 chars */
		4096,
		4,		/* 4 bits per pixel  */
		new int[] { 0x40000*8+8, 0x40000*8, 8, 0 },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8	/* every char takes 8 consecutive bytes */
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		16,16,
		4096,
		4,
		new int[] { 0x40000*8+8, 0x40000*8, 8, 0 },
		new int[] { 32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
			0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		64*8
	);
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,
		4096*2,
		4,
		new int[] { 8, 0, 0x80000*8+8, 0x80000*8 },
		new int[] { 32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
			0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		64*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,   256, 16 ),	/* Characters 8x8 */
		new GfxDecodeInfo( REGION_GFX1, 0, tile_layout,  512, 16 ), 	/* Tiles 16x16 */
		new GfxDecodeInfo( REGION_GFX2, 0, sprite_layout,  0, 16 ),	/* Sprites 16x16 */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	/******************************************************************************/
	
	static OKIM6295interface okim6295_interface = new OKIM6295interface
	(
		1,          /* 1 chip */
		new int[] { 7757 },	/* Frequency */
		new int[] { REGION_SOUND1 },      /* memory region */
		new int[] { 50 }
	);
	
	static void sound_irq(int state)
	{
		cpu_set_irq_line(1,1,state); /* IRQ 2 */
	}
	
	static YM2151interface ym2151_interface = new YM2151interface
	(
		1,
		32220000/9,
		new int[] { YM3012_VOL(45,MIXER_PAN_LEFT,45,MIXER_PAN_RIGHT) },
		new WriteYmHandlerPtr[] { sound_irq }
	);
	
	static MachineDriver machine_driver_funkyjet = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
		 	new MachineCPU(
				CPU_M68000,
				14000000, /* 28 MHz crystal */
				funkyjet_readmem,funkyjet_writemem,null,null,
				m68_level6_irq,1
			),
			new MachineCPU(
				CPU_H6280 | CPU_AUDIO_CPU, /* Custom chip 45 */
				32220000/8, /* Audio section crystal is 32.220 MHz */
				sound_readmem,sound_writemem,null,null,
				ignore_interrupt,0
			)
		},
		58, 529,
		1,
		null,
	
		/* video hardware */
		40*8, 32*8, new rectangle( 0*8, 40*8-1, 1*8, 31*8-1 ),
	
		gfxdecodeinfo,
		1024, 1024,
		null,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_UPDATE_BEFORE_VBLANK,
		null,
		funkyjet_vh_start,
		funkyjet_vh_stop,
		funkyjet_vh_screenrefresh,
	
		/* sound hardware */
		SOUND_SUPPORTS_STEREO,0,0,0,
	  	new MachineSound[] {
			new MachineSound(
				SOUND_YM2151,
				ym2151_interface
			),
			new MachineSound(
				SOUND_OKIM6295,
				okim6295_interface
			)
		}
	);
	
	/******************************************************************************/
	
	static RomLoadPtr rom_funkyjet = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1 );/* 68000 code */
		ROM_LOAD_EVEN( "jk00.12f", 0x00000, 0x40000, 0x712089c1 );
		ROM_LOAD_ODD ( "jk01.13f", 0x00000, 0x40000, 0xbe3920d7 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Sound CPU */
		ROM_LOAD( "jk02.16f",    0x00000, 0x10000, 0x748c0bd8 );
	
		ROM_REGION( 0x080000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "mat02", 0x000000, 0x80000, 0xe4b94c7e );/* chars */
	
		ROM_REGION( 0x100000, REGION_GFX2 | REGIONFLAG_DISPOSE );
	  	ROM_LOAD( "mat00", 0x000000, 0x80000, 0xfbda0228 );/* sprites */
		ROM_LOAD( "mat01", 0x080000, 0x80000, 0x24093a8d );
	
		ROM_REGION( 0x20000, REGION_SOUND1 );/* ADPCM samples */
	  	ROM_LOAD( "jk03.15h",    0x00000, 0x20000, 0x69a0eaf7 );
	ROM_END(); }}; 
	
	/******************************************************************************/
	
	public static GameDriver driver_funkyjet	   = new GameDriver("1992"	,"funkyjet"	,"funkyjet.java"	,rom_funkyjet,null	,machine_driver_funkyjet	,input_ports_funkyjet	,null	,ROT0	,	"[Data East] (Mitchell license)", "Funky Jet", GAME_NOT_WORKING )
}

/**************************************************************************

  Last Duel 			          - Capcom, 1988
  LED Storm 			          - Capcom, 1988
  Mad Gear                        - Capcom, 1989

  Emulation by Bryan McPhail, mish@tendril.co.uk

  Trivia ;)  The Mad Gear pcb has an unused pad on the board for an i8751
microcontroller.

TODO:
- The seem to be minor priority issues in Mad Gear, but the game might just
  be like that. The priority PROM is missing.
- visible area might be wrong

**************************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class lastduel
{
	
	WRITE16_HANDLER( lastduel_vram_w );
	WRITE16_HANDLER( lastduel_flip_w );
	WRITE16_HANDLER( lastduel_scroll1_w );
	WRITE16_HANDLER( lastduel_scroll2_w );
	WRITE16_HANDLER( madgear_scroll1_w );
	WRITE16_HANDLER( madgear_scroll2_w );
	WRITE16_HANDLER( lastduel_scroll_w );
	
	extern data16_t *lastduel_vram,*lastduel_scroll2,*lastduel_scroll1;
	
	/******************************************************************************/
	
	static WRITE16_HANDLER( lastduel_sound_w )
	{
		if (ACCESSING_LSB != 0)
			soundlatch_w(0,data & 0xff);
	}
	
	/******************************************************************************/
	
	static MEMORY_READ16_START( lastduel_readmem )
		{ 0x000000, 0x05ffff, MRA16_ROM },
		{ 0xfc0800, 0xfc0fff, MRA16_RAM },
		{ 0xfc4000, 0xfc4001, input_port_0_word_r },
		{ 0xfc4002, 0xfc4003, input_port_1_word_r },
		{ 0xfc4004, 0xfc4005, input_port_2_word_r },
		{ 0xfc4006, 0xfc4007, input_port_3_word_r },
		{ 0xfcc000, 0xfcdfff, MRA16_RAM },
		{ 0xfd0000, 0xfd3fff, MRA16_RAM },
		{ 0xfd4000, 0xfd7fff, MRA16_RAM },
		{ 0xfd8000, 0xfd87ff, MRA16_RAM },
		{ 0xfe0000, 0xffffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( lastduel_writemem )
		{ 0x000000, 0x05ffff, MWA16_ROM },
		{ 0xfc0000, 0xfc0003, MWA16_NOP }, /* Written rarely */
		{ 0xfc0800, 0xfc0fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0xfc4000, 0xfc4001, lastduel_flip_w },
		{ 0xfc4002, 0xfc4003, lastduel_sound_w },
		{ 0xfc8000, 0xfc8007, lastduel_scroll_w },
		{ 0xfcc000, 0xfcdfff, lastduel_vram_w, &lastduel_vram },
		{ 0xfd0000, 0xfd3fff, lastduel_scroll1_w, &lastduel_scroll1 },
		{ 0xfd4000, 0xfd7fff, lastduel_scroll2_w, &lastduel_scroll2 },
		{ 0xfd8000, 0xfd87ff, paletteram16_RRRRGGGGBBBBIIII_word_w, &paletteram16 },
		{ 0xfe0000, 0xffffff, MWA16_RAM },
	MEMORY_END
	
	static MEMORY_READ16_START( madgear_readmem )
		{ 0x000000, 0x07ffff, MRA16_ROM },
		{ 0xfc1800, 0xfc1fff, MRA16_RAM },
		{ 0xfc4000, 0xfc4001, input_port_0_word_r },
		{ 0xfc4002, 0xfc4003, input_port_1_word_r },
		{ 0xfc4004, 0xfc4005, input_port_2_word_r },
		{ 0xfc4006, 0xfc4007, input_port_3_word_r },
		{ 0xfc8000, 0xfc9fff, MRA16_RAM },
		{ 0xfcc000, 0xfcc7ff, MRA16_RAM },
		{ 0xfd4000, 0xfd7fff, MRA16_RAM },
		{ 0xfd8000, 0xfdffff, MRA16_RAM },
		{ 0xff0000, 0xffffff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( madgear_writemem )
		{ 0x000000, 0x07ffff, MWA16_ROM },
		{ 0xfc1800, 0xfc1fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0xfc4000, 0xfc4001, lastduel_flip_w },
		{ 0xfc4002, 0xfc4003, lastduel_sound_w },
		{ 0xfc8000, 0xfc9fff, lastduel_vram_w, &lastduel_vram },
		{ 0xfcc000, 0xfcc7ff, paletteram16_RRRRGGGGBBBBIIII_word_w, &paletteram16 },
		{ 0xfd0000, 0xfd0007, lastduel_scroll_w },
		{ 0xfd4000, 0xfd7fff, madgear_scroll1_w, &lastduel_scroll1 },
		{ 0xfd8000, 0xfdffff, madgear_scroll2_w, &lastduel_scroll2 },
		{ 0xff0000, 0xffffff, MWA16_RAM },
	MEMORY_END
	
	/******************************************************************************/
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xdfff, MRA_ROM ),
		new Memory_ReadAddress( 0xe000, 0xe7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xe800, 0xe800, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0xf000, 0xf000, YM2203_status_port_1_r ),
		new Memory_ReadAddress( 0xf800, 0xf800, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xdfff, MWA_ROM ),
		new Memory_WriteAddress( 0xe000, 0xe7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xe800, 0xe800, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xe801, 0xe801, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xf000, 0xf000, YM2203_control_port_1_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, YM2203_write_port_1_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static WriteHandlerPtr mg_bankswitch_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int bankaddress;
		UBytePtr RAM = memory_region(REGION_CPU2);
	
		bankaddress = 0x10000 + (data & 0x01) * 0x4000;
		cpu_setbank(3,&RAM[bankaddress]);
	} };
	
	public static Memory_ReadAddress mg_sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0xcfff, MRA_BANK3 ),
		new Memory_ReadAddress( 0xd000, 0xd7ff, MRA_RAM ),
		new Memory_ReadAddress( 0xf000, 0xf000, YM2203_status_port_0_r ),
		new Memory_ReadAddress( 0xf002, 0xf002, YM2203_status_port_1_r ),
		new Memory_ReadAddress( 0xf006, 0xf006, soundlatch_r ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress mg_sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xcfff, MWA_ROM ),
		new Memory_WriteAddress( 0xd000, 0xd7ff, MWA_RAM ),
		new Memory_WriteAddress( 0xf000, 0xf000, YM2203_control_port_0_w ),
		new Memory_WriteAddress( 0xf001, 0xf001, YM2203_write_port_0_w ),
		new Memory_WriteAddress( 0xf002, 0xf002, YM2203_control_port_1_w ),
		new Memory_WriteAddress( 0xf003, 0xf003, YM2203_write_port_1_w ),
		new Memory_WriteAddress( 0xf004, 0xf004, OKIM6295_data_0_w ),
		new Memory_WriteAddress( 0xf00a, 0xf00a, mg_bankswitch_w ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	/******************************************************************************/
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,4),
		4,
		new int[] { RGN_FRAC(0,4), RGN_FRAC(1,4), RGN_FRAC(2,4), RGN_FRAC(3,4) },
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				16*8+0, 16*8+1, 16*8+2, 16*8+3, 16*8+4, 16*8+5, 16*8+6, 16*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				8*8, 9*8, 10*8, 11*8, 12*8, 13*8, 14*8, 15*8 },
		32*8
	);
	
	static GfxLayout text_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		2,
		new int[] { 4, 0 },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
		16*8
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		4,
		new int[] { 4, 0, RGN_FRAC(1,2)+4, RGN_FRAC(1,2)+0, },
		new int[] { 0, 1, 2, 3, 8+0, 8+1, 8+2, 8+3,
				16*16+0, 16*16+1, 16*16+2, 16*16+3, 16*16+8+0, 16*16+8+1, 16*16+8+2, 16*16+8+3 },
		new int[] { 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
				8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
		32*16
	);
	
	static GfxLayout madgear_tile = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 3*4, 2*4, 1*4, 0*4 },
		new int[] { 0, 1, 2, 3, 16+0, 16+1, 16+2, 16+3,
				32*16+0, 32*16+1, 32*16+2, 32*16+3, 32*16+16+0, 32*16+16+1, 32*16+16+2, 32*16+16+3 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		64*16
	);
	
	static GfxLayout madgear_tile2 = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 1*4, 3*4, 0*4, 2*4 },
		new int[] { 0, 1, 2, 3, 16+0, 16+1, 16+2, 16+3,
				32*16+0, 32*16+1, 32*16+2, 32*16+3, 32*16+16+0, 32*16+16+1, 32*16+16+2, 32*16+16+3 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		64*16
	);
	
	static GfxDecodeInfo lastduel_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0,sprite_layout, 0x200, 16 ),	/* colors 0x200-0x2ff */
		new GfxDecodeInfo( REGION_GFX2, 0,text_layout,   0x300, 16 ),	/* colors 0x300-0x33f */
		new GfxDecodeInfo( REGION_GFX3, 0,tile_layout,   0x000, 16 ),	/* colors 0x000-0x0ff */
		new GfxDecodeInfo( REGION_GFX4, 0,tile_layout,   0x100, 16 ),	/* colors 0x100-0x1ff */
		new GfxDecodeInfo( -1 )
	};
	
	static GfxDecodeInfo madgear_gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0,sprite_layout, 0x200, 16 ),	/* colors 0x200-0x2ff */
		new GfxDecodeInfo( REGION_GFX2, 0,text_layout,   0x300, 16 ),	/* colors 0x300-0x33f */
		new GfxDecodeInfo( REGION_GFX3, 0,madgear_tile,  0x000, 16 ),	/* colors 0x000-0x0ff */
		new GfxDecodeInfo( REGION_GFX4, 0,madgear_tile2, 0x100, 16 ),	/* colors 0x100-0x1ff */
		new GfxDecodeInfo( -1 )
	};
	
	/******************************************************************************/
	
	/* handler called by the 2203 emulator when the internal timers cause an IRQ */
	static void irqhandler(int irq)
	{
		cpu_set_irq_line(1,0,irq ? ASSERT_LINE : CLEAR_LINE);
	}
	
	static OKIM6295interface okim6295_interface = new OKIM6295interface
	(
		1,              	/* 1 chip */
		new int[] { 7759 },           /* 7759Hz frequency */
		new int[] { REGION_SOUND1 },	/* memory region 3 */
		new int[] { 98 }
	);
	
	static YM2203interface ym2203_interface = new YM2203interface
	(
		2,			/* 2 chips */
		3579545, /* Accurate */
		new int[] { YM2203_VOL(40,40), YM2203_VOL(40,40) },
		new ReadHandlerPtr[] { 0 },
		new ReadHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteHandlerPtr[] { 0 },
		new WriteYmHandlerPtr[] { irqhandler }
	);
	
	public static InterruptPtr lastduel_interrupt = new InterruptPtr() { public int handler() 
	{
		if (cpu_getiloops() == 0) return 2; /* VBL */
		else return 4; /* Controls */
	} };
	
	public static InterruptPtr madgear_interrupt = new InterruptPtr() { public int handler() 
	{
		if (cpu_getiloops() == 0) return 5; /* VBL */
		else return 6; /* Controls */
	} };
	
	static MachineDriver machine_driver_lastduel = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				10000000, /* Could be 8 MHz */
				lastduel_readmem, lastduel_writemem, null,null,
				lastduel_interrupt,3	/* 1 for vbl, 2 for control reads?? */
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3579545, /* Accurate */
				sound_readmem,sound_writemem,null,null,
				ignore_interrupt,0	/* IRQs are caused by the YM2203 */
			)
		},
		60, DEFAULT_60HZ_VBLANK_DURATION,
		1,
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 8*8, (64-8)*8-1, 1*8, 31*8-1 ),
	
		lastduel_gfxdecodeinfo,
		1024, 1024,
		0,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_BUFFERS_SPRITERAM,
		lastduel_eof_callback,
		lastduel_vh_start,
		null,
		lastduel_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_YM2203,
				ym2203_interface
			)
		}
	);
	
	static MachineDriver machine_driver_madgear = new MachineDriver
	(
		/* basic machine hardware */
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				10000000, /* Accurate */
				madgear_readmem, madgear_writemem, null,null,
				madgear_interrupt,3	/* 1 for vbl, 2 for control reads?? */
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3579545, /* Accurate */
				mg_sound_readmem,mg_sound_writemem,null,null,
				ignore_interrupt,0	/* IRQs are caused by the YM2203 */
			)
		},
		60, DEFAULT_60HZ_VBLANK_DURATION,
		1,
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 8*8, (64-8)*8-1, 1*8, 31*8-1 ),
	
		madgear_gfxdecodeinfo,
		1024, 1024,
		0,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_UPDATE_BEFORE_VBLANK | VIDEO_BUFFERS_SPRITERAM,
		lastduel_eof_callback,
		madgear_vh_start,
		null,
		lastduel_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_YM2203,
				ym2203_interface
			),
			new MachineSound(
				SOUND_OKIM6295,
				okim6295_interface
			)
		}
	);
	
	/******************************************************************************/
	
	static InputPortPtr input_ports_lastduel = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_UNUSED );
	
		PORT_START(); 
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_SERVICE( 0x08, IP_ACTIVE_LOW );
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN2 );
	
		PORT_START(); 
		PORT_DIPNAME( 0x0007, 0x0007, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0007, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0006, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0005, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0004, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0003, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x0038, 0x0038, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0008, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0010, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0038, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0030, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0028, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0020, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0018, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0300, 0x0300, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0200, "Easy" );
		PORT_DIPSETTING(      0x0300, "Normal" );
		PORT_DIPSETTING(      0x0100, "Difficult" );
		PORT_DIPSETTING(      0x0000, "Very Difficult" );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unknown") );	/* Could be cabinet type? */
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x3000, 0x3000, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(      0x2000, "20000 60000 80000" );
		PORT_DIPSETTING(      0x3000, "30000 80000 80000" );
		PORT_DIPSETTING(      0x1000, "40000 80000 80000" );
		PORT_DIPSETTING(      0x0000, "40000 80000 100000" );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
	
		PORT_START(); 
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "6" );
		PORT_DIPSETTING(    0x00, "8" );
		PORT_DIPNAME( 0x04, 0x04, "Type" );
		PORT_DIPSETTING(    0x04, "Car" );
		PORT_DIPSETTING(    0x00, "Plane" );
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x08, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_madgear = new InputPortPtr(){ public void handler() { 
		PORT_START(); 
		PORT_DIPNAME( 0x0001, 0x0001, "Allow Continue" );
		PORT_DIPSETTING(      0x0000, DEF_STR( "No") );
		PORT_DIPSETTING(      0x0001, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x0002, 0x0002, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(      0x0002, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x000c, 0x000c, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(      0x0008, "Easy" );
		PORT_DIPSETTING(      0x000c, "Normal" );
		PORT_DIPSETTING(      0x0004, "Difficult" );
		PORT_DIPSETTING(      0x0000, "Very Difficult" );
		PORT_DIPNAME( 0x0030, 0x0030, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(      0x0030, "Upright One Player" );
		PORT_DIPSETTING(      0x0000, "Upright Two Players" );
		PORT_DIPSETTING(      0x0010, DEF_STR( "Cocktail") );
	/* 	PORT_DIPSETTING(      0x0020, "Upright One Player" );*/
		PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0040, DEF_STR( "On") );
		PORT_DIPNAME( 0x0080, 0x0080, "Background Music" );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0080, DEF_STR( "On") );
		PORT_DIPNAME( 0x0100, 0x0100, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0200, 0x0200, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0400, 0x0400, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x0800, 0x0800, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x1000, 0x1000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x2000, 0x2000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( "Unknown") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "Off") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "On") );
		PORT_SERVICE( 0x8000, IP_ACTIVE_LOW );
	
		PORT_START();  /* Dip switch C, free play is COIN B all off, COIN A all on */
		PORT_DIPNAME( 0x0f00, 0x0f00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(      0x0200, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(      0x0400, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x0500, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x0700, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x0100, DEF_STR( "8C_3C") );
		PORT_DIPSETTING(      0x0900, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x0300, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(      0x0600, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0x0f00, DEF_STR( "1C_1C") );
	//	PORT_DIPSETTING(      0x0000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x0800, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0x0e00, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0x0d00, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0x0c00, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0x0b00, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0x0a00, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0xf000, 0xf000, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(      0x2000, DEF_STR( "6C_1C") );
		PORT_DIPSETTING(      0x4000, DEF_STR( "5C_1C") );
		PORT_DIPSETTING(      0x5000, DEF_STR( "4C_1C") );
		PORT_DIPSETTING(      0x7000, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(      0x1000, DEF_STR( "8C_3C") );
		PORT_DIPSETTING(      0x9000, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(      0x3000, DEF_STR( "5C_3C") );
		PORT_DIPSETTING(      0x6000, DEF_STR( "3C_2C") );
		PORT_DIPSETTING(      0xf000, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(      0x8000, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(      0xe000, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(      0xd000, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(      0xc000, DEF_STR( "1C_4C") );
		PORT_DIPSETTING(      0xb000, DEF_STR( "1C_5C") );
		PORT_DIPSETTING(      0xa000, DEF_STR( "1C_6C") );
		PORT_DIPSETTING(      0x0000, DEF_STR( "Free_Play") );
	
		PORT_START(); 
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_UNUSED );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
	
		PORT_START(); 
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_SERVICE1 );
	INPUT_PORTS_END(); }}; 
	
	/******************************************************************************/
	
	static RomLoadPtr rom_lastduel = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1 );/* 68000 code */
		ROM_LOAD_EVEN( "ldu-06.rom",   0x00000, 0x20000, 0x4228a00b );
		ROM_LOAD_ODD ( "ldu-05.rom",   0x00000, 0x20000, 0x7260434f );
		ROM_LOAD_EVEN( "ldu-04.rom",   0x40000, 0x10000, 0x429fb964 );
		ROM_LOAD_ODD ( "ldu-03.rom",   0x40000, 0x10000, 0x5aa4df72 );
	
		ROM_REGION( 0x10000 , REGION_CPU2 );/* audio CPU */
		ROM_LOAD( "ld_02.bin",    0x0000, 0x10000, 0x91834d0c );
	
		ROM_REGION( 0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_09.bin",    0x000000, 0x10000, 0xf8fd5243 );/* sprites */
		ROM_LOAD( "ld_10.bin",    0x010000, 0x10000, 0xb49ad746 );
		ROM_LOAD( "ld_11.bin",    0x020000, 0x10000, 0x1a0d180e );
		ROM_LOAD( "ld_12.bin",    0x030000, 0x10000, 0xb2745e26 );
		ROM_LOAD( "ld_15.bin",    0x040000, 0x10000, 0x96b13bbc );
		ROM_LOAD( "ld_16.bin",    0x050000, 0x10000, 0x9d80f7e6 );
		ROM_LOAD( "ld_13.bin",    0x060000, 0x10000, 0xa1a598ac );
		ROM_LOAD( "ld_14.bin",    0x070000, 0x10000, 0xedf515cc );
	
		ROM_REGION( 0x08000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_01.bin",    0x000000, 0x08000, 0xad3c6f87 );/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_17.bin",    0x000000, 0x10000, 0x7188bfdd );/* tiles */
		ROM_LOAD( "ld_18.bin",    0x010000, 0x10000, 0xa62af66a );
		ROM_LOAD( "ld_19.bin",    0x020000, 0x10000, 0x4b762e50 );
		ROM_LOAD( "ld_20.bin",    0x030000, 0x10000, 0xb140188e );
	
		ROM_REGION( 0x80000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_28.bin",    0x000000, 0x10000, 0x06778248 );/* tiles */
		ROM_LOAD( "ld_26.bin",    0x010000, 0x10000, 0xb0edac81 );
		ROM_LOAD( "ld_24.bin",    0x020000, 0x10000, 0x66eac4df );
		ROM_LOAD( "ld_22.bin",    0x030000, 0x10000, 0xf80f8812 );
		ROM_LOAD( "ld_27.bin",    0x040000, 0x10000, 0x48c78675 );
		ROM_LOAD( "ld_25.bin",    0x050000, 0x10000, 0xc541ae9a );
		ROM_LOAD( "ld_23.bin",    0x060000, 0x10000, 0xd817332c );
		ROM_LOAD( "ld_21.bin",    0x070000, 0x10000, 0xb74f0c0e );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "63s141.3d",    0x0000, 0x0100, 0x729a1ddc );/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_lstduela = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1 );/* 68000 code */
		ROM_LOAD_EVEN( "06",   0x00000, 0x20000, 0x0e71acaf );
		ROM_LOAD_ODD ( "05",   0x00000, 0x20000, 0x47a85bea );
		ROM_LOAD_EVEN( "04",   0x40000, 0x10000, 0xaa4bf001 );
		ROM_LOAD_ODD ( "03",   0x40000, 0x10000, 0xbbaac8ab );
	
		ROM_REGION( 0x10000 , REGION_CPU2 );/* audio CPU */
		ROM_LOAD( "ld_02.bin",    0x0000, 0x10000, 0x91834d0c );
	
		ROM_REGION( 0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_09.bin",    0x000000, 0x10000, 0xf8fd5243 );/* sprites */
		ROM_LOAD( "ld_10.bin",    0x010000, 0x10000, 0xb49ad746 );
		ROM_LOAD( "ld_11.bin",    0x020000, 0x10000, 0x1a0d180e );
		ROM_LOAD( "ld_12.bin",    0x030000, 0x10000, 0xb2745e26 );
		ROM_LOAD( "ld_15.bin",    0x040000, 0x10000, 0x96b13bbc );
		ROM_LOAD( "ld_16.bin",    0x050000, 0x10000, 0x9d80f7e6 );
		ROM_LOAD( "ld_13.bin",    0x060000, 0x10000, 0xa1a598ac );
		ROM_LOAD( "ld_14.bin",    0x070000, 0x10000, 0xedf515cc );
	
		ROM_REGION( 0x08000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_01.bin",    0x000000, 0x08000, 0xad3c6f87 );/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_17.bin",    0x000000, 0x10000, 0x7188bfdd );/* tiles */
		ROM_LOAD( "ld_18.bin",    0x010000, 0x10000, 0xa62af66a );
		ROM_LOAD( "ld_19.bin",    0x020000, 0x10000, 0x4b762e50 );
		ROM_LOAD( "ld_20.bin",    0x030000, 0x10000, 0xb140188e );
	
		ROM_REGION( 0x80000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_28.bin",    0x000000, 0x10000, 0x06778248 );/* tiles */
		ROM_LOAD( "ld_26.bin",    0x010000, 0x10000, 0xb0edac81 );
		ROM_LOAD( "ld_24.bin",    0x020000, 0x10000, 0x66eac4df );
		ROM_LOAD( "ld_22.bin",    0x030000, 0x10000, 0xf80f8812 );
		ROM_LOAD( "ld_27.bin",    0x040000, 0x10000, 0x48c78675 );
		ROM_LOAD( "ld_25.bin",    0x050000, 0x10000, 0xc541ae9a );
		ROM_LOAD( "ld_23.bin",    0x060000, 0x10000, 0xd817332c );
		ROM_LOAD( "ld_21.bin",    0x070000, 0x10000, 0xb74f0c0e );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "63s141.3d",    0x0000, 0x0100, 0x729a1ddc );/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_lstduelb = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1 );/* 68000 code */
		ROM_LOAD_EVEN( "ld_08.bin",    0x00000, 0x10000, 0x43811a96 );
		ROM_LOAD_ODD ( "ld_07.bin",    0x00000, 0x10000, 0x63c30946 );
		ROM_LOAD_EVEN( "ld_04.bin",    0x20000, 0x10000, 0x46a4e0f8 );
		ROM_LOAD_ODD ( "ld_03.bin",    0x20000, 0x10000, 0x8d5f204a );
		ROM_LOAD_EVEN( "ldu-04.rom",   0x40000, 0x10000, 0x429fb964 );
		ROM_LOAD_ODD ( "ldu-03.rom",   0x40000, 0x10000, 0x5aa4df72 );
	
		ROM_REGION( 0x10000 , REGION_CPU2 );/* audio CPU */
		ROM_LOAD( "ld_02.bin",    0x0000, 0x10000, 0x91834d0c );
	
		ROM_REGION( 0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_09.bin",    0x000000, 0x10000, 0xf8fd5243 );/* sprites */
		ROM_LOAD( "ld_10.bin",    0x010000, 0x10000, 0xb49ad746 );
		ROM_LOAD( "ld_11.bin",    0x020000, 0x10000, 0x1a0d180e );
		ROM_LOAD( "ld_12.bin",    0x030000, 0x10000, 0xb2745e26 );
		ROM_LOAD( "ld_15.bin",    0x040000, 0x10000, 0x96b13bbc );
		ROM_LOAD( "ld_16.bin",    0x050000, 0x10000, 0x9d80f7e6 );
		ROM_LOAD( "ld_13.bin",    0x060000, 0x10000, 0xa1a598ac );
		ROM_LOAD( "ld_14.bin",    0x070000, 0x10000, 0xedf515cc );
	
		ROM_REGION( 0x08000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_01.bin",    0x000000, 0x08000, 0xad3c6f87 );/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_17.bin",    0x000000, 0x10000, 0x7188bfdd );/* tiles */
		ROM_LOAD( "ld_18.bin",    0x010000, 0x10000, 0xa62af66a );
		ROM_LOAD( "ld_19.bin",    0x020000, 0x10000, 0x4b762e50 );
		ROM_LOAD( "ld_20.bin",    0x030000, 0x10000, 0xb140188e );
	
		ROM_REGION( 0x80000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ld_28.bin",    0x000000, 0x10000, 0x06778248 );/* tiles */
		ROM_LOAD( "ld_26.bin",    0x010000, 0x10000, 0xb0edac81 );
		ROM_LOAD( "ld_24.bin",    0x020000, 0x10000, 0x66eac4df );
		ROM_LOAD( "ld_22.bin",    0x030000, 0x10000, 0xf80f8812 );
		ROM_LOAD( "ld_27.bin",    0x040000, 0x10000, 0x48c78675 );
		ROM_LOAD( "ld_25.bin",    0x050000, 0x10000, 0xc541ae9a );
		ROM_LOAD( "ld_23.bin",    0x060000, 0x10000, 0xd817332c );
		ROM_LOAD( "ld_21.bin",    0x070000, 0x10000, 0xb74f0c0e );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "63s141.3d",    0x0000, 0x0100, 0x729a1ddc );/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_madgear = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1 );/* 256K for 68000 code */
		ROM_LOAD_EVEN( "mg_04.rom",    0x00000, 0x20000, 0xb112257d );
		ROM_LOAD_ODD ( "mg_03.rom",    0x00000, 0x20000, 0xb2672465 );
		ROM_LOAD_EVEN( "mg_02.rom",    0x40000, 0x20000, 0x9f5ebe16 );
		ROM_LOAD_ODD ( "mg_01.rom",    0x40000, 0x20000, 0x1cea2af0 );
	
		ROM_REGION( 0x18000 , REGION_CPU2 );/* audio CPU */
		ROM_LOAD( "mg_05.rom",    0x00000,  0x08000, 0x2fbfc945 );
		ROM_CONTINUE(             0x10000,  0x08000 );
	
		ROM_REGION( 0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "mg_m11.rom",   0x000000, 0x10000, 0xee319a64 );/* Interleaved sprites */
		ROM_LOAD( "mg_m07.rom",   0x010000, 0x10000, 0xe5c0b211 );
		ROM_LOAD( "mg_m12.rom",   0x020000, 0x10000, 0x887ef120 );
		ROM_LOAD( "mg_m08.rom",   0x030000, 0x10000, 0x59709aa3 );
		ROM_LOAD( "mg_m13.rom",   0x040000, 0x10000, 0xeae07db4 );
		ROM_LOAD( "mg_m09.rom",   0x050000, 0x10000, 0x40ee83eb );
		ROM_LOAD( "mg_m14.rom",   0x060000, 0x10000, 0x21e5424c );
		ROM_LOAD( "mg_m10.rom",   0x070000, 0x10000, 0xb64afb54 );
	
		ROM_REGION( 0x08000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "mg_06.rom",    0x000000, 0x08000, 0x382ee59b );/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ls-12",        0x000000, 0x40000, 0x6c1b2c6c );
	
		ROM_REGION( 0x80000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ls-11",        0x000000, 0x80000, 0x6bf81c64 );
	
		ROM_REGION( 0x40000, REGION_SOUND1 );/* ADPCM */
		ROM_LOAD( "ls-06",        0x00000, 0x20000, 0x88d39a5b );
		ROM_LOAD( "ls-05",        0x20000, 0x20000, 0xb06e03b5 );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "prom",         0x0000, 0x0100, 0x00000000 );/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_madgearj = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1 );/* 256K for 68000 code */
		ROM_LOAD_EVEN( "mdj_04.rom",   0x00000, 0x20000, 0x9ebbebb1 );
		ROM_LOAD_ODD ( "mdj_03.rom",   0x00000, 0x20000, 0xa5579c2d );
		ROM_LOAD_EVEN( "mg_02.rom",    0x40000, 0x20000, 0x9f5ebe16 );
		ROM_LOAD_ODD ( "mg_01.rom",    0x40000, 0x20000, 0x1cea2af0 );
	
		ROM_REGION(  0x18000 , REGION_CPU2 );/* audio CPU */
		ROM_LOAD( "mg_05.rom",    0x00000,  0x08000, 0x2fbfc945 );
		ROM_CONTINUE(             0x10000,  0x08000 );
	
		ROM_REGION( 0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "mg_m11.rom",   0x000000, 0x10000, 0xee319a64 );/* Interleaved sprites */
		ROM_LOAD( "mg_m07.rom",   0x010000, 0x10000, 0xe5c0b211 );
		ROM_LOAD( "mg_m12.rom",   0x020000, 0x10000, 0x887ef120 );
		ROM_LOAD( "mg_m08.rom",   0x030000, 0x10000, 0x59709aa3 );
		ROM_LOAD( "mg_m13.rom",   0x040000, 0x10000, 0xeae07db4 );
		ROM_LOAD( "mg_m09.rom",   0x050000, 0x10000, 0x40ee83eb );
		ROM_LOAD( "mg_m14.rom",   0x060000, 0x10000, 0x21e5424c );
		ROM_LOAD( "mg_m10.rom",   0x070000, 0x10000, 0xb64afb54 );
	
		ROM_REGION( 0x08000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "mg_06.rom",    0x000000, 0x08000, 0x382ee59b );/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ls-12",        0x000000, 0x40000, 0x6c1b2c6c );
	
		ROM_REGION( 0x80000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ls-11",        0x000000, 0x80000, 0x6bf81c64 );
	
		ROM_REGION( 0x40000, REGION_SOUND1 );/* ADPCM */
		ROM_LOAD( "ls-06",        0x00000, 0x20000, 0x88d39a5b );
		ROM_LOAD( "ls-05",        0x20000, 0x20000, 0xb06e03b5 );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "prom",         0x0000, 0x0100, 0x00000000 );/* priority (not used) */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_ledstorm = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x80000, REGION_CPU1 );/* 256K for 68000 code */
		ROM_LOAD_EVEN( "mdu.04",    0x00000, 0x20000, 0x7f7f8329 );
		ROM_LOAD_ODD ( "mdu.03",    0x00000, 0x20000, 0x11fa542f );
		ROM_LOAD_EVEN( "mg_02.rom", 0x40000, 0x20000, 0x9f5ebe16 );
		ROM_LOAD_ODD ( "mg_01.rom", 0x40000, 0x20000, 0x1cea2af0 );
	
		ROM_REGION(  0x18000 , REGION_CPU2 );/* audio CPU */
		ROM_LOAD( "mg_05.rom",    0x00000,  0x08000, 0x2fbfc945 );
		ROM_CONTINUE(             0x10000,  0x08000 );
	
		ROM_REGION( 0x80000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "mg_m11.rom",   0x000000, 0x10000, 0xee319a64 );/* Interleaved sprites */
		ROM_LOAD( "07",           0x010000, 0x10000, 0x7152b212 );
		ROM_LOAD( "mg_m12.rom",   0x020000, 0x10000, 0x887ef120 );
		ROM_LOAD( "08",           0x030000, 0x10000, 0x72e5d525 );
		ROM_LOAD( "mg_m13.rom",   0x040000, 0x10000, 0xeae07db4 );
		ROM_LOAD( "09",           0x050000, 0x10000, 0x7b5175cb );
		ROM_LOAD( "mg_m14.rom",   0x060000, 0x10000, 0x21e5424c );
		ROM_LOAD( "10",           0x070000, 0x10000, 0x6db7ca64 );
	
		ROM_REGION( 0x08000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "06",           0x000000, 0x08000, 0x54bfdc02 );/* 8x8 text */
	
		ROM_REGION( 0x40000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ls-12",        0x000000, 0x40000, 0x6c1b2c6c );
	
		ROM_REGION( 0x80000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "ls-11",        0x000000, 0x80000, 0x6bf81c64 );
	
		ROM_REGION( 0x40000, REGION_SOUND1 );/* ADPCM */
		ROM_LOAD( "ls-06",        0x00000, 0x20000, 0x88d39a5b );
		ROM_LOAD( "ls-05",        0x20000, 0x20000, 0xb06e03b5 );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "prom",         0x0000, 0x0100, 0x00000000 );/* priority (not used) */
	ROM_END(); }}; 
	
	/******************************************************************************/
	
	public static GameDriver driver_lastduel	   = new GameDriver("1988"	,"lastduel"	,"lastduel.java"	,rom_lastduel,null	,machine_driver_lastduel	,input_ports_lastduel	,null	,ROT270	,	"Capcom", "Last Duel (US set 1)" )
	public static GameDriver driver_lstduela	   = new GameDriver("1988"	,"lstduela"	,"lastduel.java"	,rom_lstduela,driver_lastduel	,machine_driver_lastduel	,input_ports_lastduel	,null	,ROT270	,	"Capcom", "Last Duel (US set 2)" )
	public static GameDriver driver_lstduelb	   = new GameDriver("1988"	,"lstduelb"	,"lastduel.java"	,rom_lstduelb,driver_lastduel	,machine_driver_lastduel	,input_ports_lastduel	,null	,ROT270	,	"bootleg", "Last Duel (bootleg)" )
	public static GameDriver driver_madgear	   = new GameDriver("1989"	,"madgear"	,"lastduel.java"	,rom_madgear,null	,machine_driver_madgear	,input_ports_madgear	,null	,ROT270	,	"Capcom", "Mad Gear (US)" )
	public static GameDriver driver_madgearj	   = new GameDriver("1989"	,"madgearj"	,"lastduel.java"	,rom_madgearj,driver_madgear	,machine_driver_madgear	,input_ports_madgear	,null	,ROT270	,	"Capcom", "Mad Gear (Japan)" )
	public static GameDriver driver_ledstorm	   = new GameDriver("1988"	,"ledstorm"	,"lastduel.java"	,rom_ledstorm,driver_madgear	,machine_driver_madgear	,input_ports_madgear	,null	,ROT270	,	"Capcom", "Led Storm (US)" )
}

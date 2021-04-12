/**********************************************************************

Armed Formation
(c)1988 Nichibutsu

Terra Force
(c)1987 Nichibutsu

Kodure Ookami
(c)1987 Nichibutsu

Crazy Climber 2
(c)1988 Nichibutsu

68000 + Z80

TODO:
- simulate the mcu/blitter (particularly needed in terrafu)

***********************************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class armedf
{
	
	
	WRITE16_HANDLER( armedf_bg_videoram_w );
	WRITE16_HANDLER( armedf_fg_videoram_w );
	WRITE16_HANDLER( terraf_text_videoram_w );
	WRITE16_HANDLER( armedf_text_videoram_w );
	WRITE16_HANDLER( terraf_fg_scrollx_w );
	WRITE16_HANDLER( terraf_fg_scrolly_w );
	WRITE16_HANDLER( terraf_fg_scroll_msb_arm_w );
	WRITE16_HANDLER( armedf_fg_scrollx_w );
	WRITE16_HANDLER( armedf_fg_scrolly_w );
	WRITE16_HANDLER( armedf_bg_scrollx_w );
	WRITE16_HANDLER( armedf_bg_scrolly_w );
	
	extern data16_t armedf_vreg;
	extern data16_t *armedf_bg_videoram;
	extern data16_t *armedf_fg_videoram;
	extern data16_t *terraf_text_videoram;
	
	
	static WRITE16_HANDLER( io_w )
	{
		COMBINE_DATA(&armedf_vreg);
		/* bits 0 and 1 of armedf_vreg are coin counters */
	}
	
	static WRITE16_HANDLER( kodure_io_w )
	{
		COMBINE_DATA(&armedf_vreg);
		/* bits 0 and 1 of armedf_vreg are coin counters */
	
		/* This is a temporary condition specification. */
		if (!(armedf_vreg & 0x0080))
		{
			int i;
			for (i = 0; i < 0x1000; i++)
			{
				terraf_text_videoram_w(i, ' ', 0);
			}
		}
	}
	
	static WRITE16_HANDLER( sound_command_w )
	{
		if (ACCESSING_LSB != 0)
			soundlatch_w(0,((data & 0x7f) << 1) | 1);
	}
	
	
	
	static MEMORY_READ16_START( terraf_readmem )
		{ 0x000000, 0x04ffff, MRA16_ROM },
		{ 0x060000, 0x063fff, MRA16_RAM },
		{ 0x064000, 0x064fff, MRA16_RAM },
		{ 0x068000, 0x069fff, MRA16_RAM },
		{ 0x06a000, 0x06a9ff, MRA16_RAM },
		{ 0x06C000, 0x06C9ff, MRA16_RAM },
		{ 0x070000, 0x070fff, MRA16_RAM },
		{ 0x074000, 0x074fff, MRA16_RAM },
		{ 0x078000, 0x078001, input_port_0_word_r },
		{ 0x078002, 0x078003, input_port_1_word_r },
		{ 0x078004, 0x078005, input_port_2_word_r },
		{ 0x078006, 0x078007, input_port_3_word_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( terraf_writemem )
		{ 0x000000, 0x04ffff, MWA16_ROM },
		{ 0x060000, 0x0603ff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x060400, 0x063fff, MWA16_RAM },
		{ 0x064000, 0x064fff, paletteram16_xxxxRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0x068000, 0x069fff, terraf_text_videoram_w, &terraf_text_videoram },
		{ 0x06a000, 0x06a9ff, MWA16_RAM },
		{ 0x06C000, 0x06C9ff, MWA16_RAM },
		{ 0x070000, 0x070fff, armedf_fg_videoram_w, &armedf_fg_videoram },
		{ 0x074000, 0x074fff, armedf_bg_videoram_w, &armedf_bg_videoram },
		{ 0x07c000, 0x07c001, io_w },
		{ 0x07c002, 0x07c003, armedf_bg_scrollx_w },
		{ 0x07c004, 0x07c005, armedf_bg_scrolly_w },
		{ 0x07c006, 0x07c007, terraf_fg_scrollx_w },
		{ 0x07c008, 0x07c009, terraf_fg_scrolly_w },	/* written twice, lsb and msb */
		{ 0x07c00a, 0x07c00b, sound_command_w },
		{ 0x0c0000, 0x0c0001, terraf_fg_scroll_msb_arm_w }, /* written between two consecutive writes to 7c008 */
	MEMORY_END
	
	static MEMORY_READ16_START( kodure_readmem )
		{ 0x000000, 0x05ffff, MRA16_ROM },
		{ 0x060000, 0x063fff, MRA16_RAM },
		{ 0x064000, 0x064fff, MRA16_RAM },
		{ 0x068000, 0x069fff, MRA16_RAM },
		{ 0x06a000, 0x06a9ff, MRA16_RAM },
		{ 0x06C000, 0x06C9ff, MRA16_RAM },
		{ 0x070000, 0x070fff, MRA16_RAM },
		{ 0x074000, 0x074fff, MRA16_RAM },
		{ 0x078000, 0x078001, input_port_0_word_r },
		{ 0x078002, 0x078003, input_port_1_word_r },
		{ 0x078004, 0x078005, input_port_2_word_r },
		{ 0x078006, 0x078007, input_port_3_word_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( kodure_writemem )
		{ 0x000000, 0x05ffff, MWA16_ROM },
		{ 0x060000, 0x060fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x061000, 0x063fff, MWA16_RAM },
		{ 0x064000, 0x064fff, paletteram16_xxxxRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0x068000, 0x069fff, terraf_text_videoram_w, &terraf_text_videoram },
		{ 0x06a000, 0x06a9ff, MWA16_RAM },
		{ 0x06C000, 0x06C9ff, MWA16_RAM },
		{ 0x070000, 0x070fff, armedf_fg_videoram_w, &armedf_fg_videoram },
		{ 0x074000, 0x074fff, armedf_bg_videoram_w, &armedf_bg_videoram },
		{ 0x07c000, 0x07c001, kodure_io_w },
		{ 0x07c002, 0x07c003, armedf_bg_scrollx_w },
		{ 0x07c004, 0x07c005, armedf_bg_scrolly_w },
		{ 0x07c00a, 0x07c00b, sound_command_w },
		{ 0x0c0000, 0x0c0001, MWA16_NOP }, /* watchdog? */
		{ 0xffd000, 0xffd001, MWA16_NOP }, /* ? */
	MEMORY_END
	
	static MEMORY_READ16_START( armedf_readmem )
		{ 0x000000, 0x05ffff, MRA16_ROM },
		{ 0x060000, 0x065fff, MRA16_RAM },
		{ 0x066000, 0x066fff, MRA16_RAM },
		{ 0x067000, 0x067fff, MRA16_RAM },
		{ 0x068000, 0x069fff, MRA16_RAM },
		{ 0x06a000, 0x06afff, MRA16_RAM },
		{ 0x06b000, 0x06bfff, MRA16_RAM },
		{ 0x06c000, 0x06c001, input_port_0_word_r },
		{ 0x06c002, 0x06c003, input_port_1_word_r },
		{ 0x06c004, 0x06c005, input_port_2_word_r },
		{ 0x06c006, 0x06c007, input_port_3_word_r },
		{ 0x06c008, 0x06c7ff, MRA16_RAM },
	MEMORY_END
	
	static MEMORY_WRITE16_START( armedf_writemem )
		{ 0x000000, 0x05ffff, MWA16_ROM },
		{ 0x060000, 0x060fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x061000, 0x065fff, MWA16_RAM },
		{ 0x066000, 0x066fff, armedf_bg_videoram_w, &armedf_bg_videoram },
		{ 0x067000, 0x067fff, armedf_fg_videoram_w, &armedf_fg_videoram },
		{ 0x068000, 0x069fff, armedf_text_videoram_w, &terraf_text_videoram },
		{ 0x06a000, 0x06afff, paletteram16_xxxxRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0x06b000, 0x06bfff, MWA16_RAM },
		{ 0x06c000, 0x06c7ff, MWA16_RAM },
		{ 0x06d000, 0x06d001, io_w },
		{ 0x06d002, 0x06d003, armedf_bg_scrollx_w },
		{ 0x06d004, 0x06d005, armedf_bg_scrolly_w },
		{ 0x06d006, 0x06d007, armedf_fg_scrollx_w },
		{ 0x06d008, 0x06d009, armedf_fg_scrolly_w },
		{ 0x06d00a, 0x06d00b, sound_command_w },
	MEMORY_END
	
	static MEMORY_READ16_START( cclimbr2_readmem )
		{ 0x000000, 0x05ffff, MRA16_ROM },
		{ 0x060000, 0x063fff, MRA16_RAM },
		{ 0x064000, 0x064fff, MRA16_RAM },
		{ 0x068000, 0x069fff, MRA16_RAM },
		{ 0x06a000, 0x06a9ff, MRA16_RAM },
		{ 0x06c000, 0x06c9ff, MRA16_RAM },
		{ 0x070000, 0x070fff, MRA16_RAM },
		{ 0x074000, 0x074fff, MRA16_RAM },
		{ 0x078000, 0x078001, input_port_0_word_r },
		{ 0x078002, 0x078003, input_port_1_word_r },
		{ 0x078004, 0x078005, input_port_2_word_r },
		{ 0x078006, 0x078007, input_port_3_word_r },
	MEMORY_END
	
	static MEMORY_WRITE16_START( cclimbr2_writemem )
		{ 0x000000, 0x05ffff, MWA16_ROM },
		{ 0x060000, 0x060fff, MWA16_RAM, &spriteram16, &spriteram_size },
		{ 0x061000, 0x063fff, MWA16_RAM },
		{ 0x064000, 0x064fff, paletteram16_xxxxRRRRGGGGBBBB_word_w, &paletteram16 },
		{ 0x068000, 0x069fff, terraf_text_videoram_w, &terraf_text_videoram },
		{ 0x06a000, 0x06a9ff, MWA16_RAM },
		{ 0x06c000, 0x06c9ff, MWA16_RAM },
		{ 0x06ca00, 0x06cbff, MWA16_RAM },
		{ 0x070000, 0x070fff, armedf_fg_videoram_w, &armedf_fg_videoram },
		{ 0x074000, 0x074fff, armedf_bg_videoram_w, &armedf_bg_videoram },
		{ 0x07c000, 0x07c001, io_w },
		{ 0x07c002, 0x07c003, armedf_bg_scrollx_w },
		{ 0x07c004, 0x07c005, armedf_bg_scrolly_w },
		{ 0x07c00a, 0x07c00b, sound_command_w },
	MEMORY_END
	
	
	public static Memory_ReadAddress soundreadmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xf7ff, MRA_ROM ),
		new Memory_ReadAddress( 0xf800, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress soundwritemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xf7ff, MWA_ROM ),
		new Memory_WriteAddress( 0xf800, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_ReadAddress cclimbr2_soundreadmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0xbfff, MRA_ROM ),
		new Memory_ReadAddress( 0xc000, 0xffff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress cclimbr2_soundwritemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0xbfff, MWA_ROM ),
		new Memory_WriteAddress( 0xc000, 0xffff, MWA_RAM ),
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static ReadHandlerPtr soundlatch_clear_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		soundlatch_clear_w(0,0);
		return 0;
	} };
	
	public static IO_ReadPort readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x4, 0x4, soundlatch_clear_r ),
		new IO_ReadPort( 0x6, 0x6, soundlatch_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x0, 0x0, YM3812_control_port_0_w ),
		new IO_WritePort( 0x1, 0x1, YM3812_write_port_0_w ),
	  	new IO_WritePort( 0x2, 0x2, DAC_0_signed_data_w ),
	  	new IO_WritePort( 0x3, 0x3, DAC_1_signed_data_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	static InputPortPtr input_ports_armedf = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY);
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY);
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY);
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY);
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0xf000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2);
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2);
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2);
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2);
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_SERVICE( 0x0200, IP_ACTIVE_LOW );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_TILT );    /* Tilt */
		PORT_BIT( 0xf800, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x04, 0x04, "First Bonus" );
		PORT_DIPSETTING(    0x04, "20k" );
		PORT_DIPSETTING(    0x00, "40k" );
		PORT_DIPNAME( 0x08, 0x08, "Second Bonus" );
		PORT_DIPSETTING(    0x08, "60k" );
		PORT_DIPSETTING(    0x00, "80k" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xc0, "Easy" );
		PORT_DIPSETTING(    0x80, "Normal" );
		PORT_DIPSETTING(    0x40, "Hard" );
		PORT_DIPSETTING(    0x00, "Very Hard" );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, "Allow Continue" );
		PORT_DIPSETTING(    0x20, DEF_STR( "No") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x40, 0x40, "Flip Screen ?" );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_terraf = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY);
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY);
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY);
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY);
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0xf000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2);
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2);
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2);
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2);
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2);
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2);
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_SERVICE( 0x0200, IP_ACTIVE_LOW );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_TILT );    /* Tilt */
		PORT_BIT( 0xf800, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x0c, "20k" );
		PORT_DIPSETTING(    0x08, "50k" );
		PORT_DIPSETTING(    0x04, "60k" );
		PORT_DIPSETTING(    0x00, "90k" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0xc0, 0xc0, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0xc0, "Easy" );
		PORT_DIPSETTING(    0x80, "Normal" );
		PORT_DIPSETTING(    0x40, "Hard" );
		PORT_DIPSETTING(    0x00, "Very Hard" );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0xc0, "Allow Continue" );
		PORT_DIPSETTING(    0xc0, "0" );
		PORT_DIPSETTING(    0x80, "3" );
		PORT_DIPSETTING(    0x40, "5" );
		PORT_DIPSETTING(    0x00, "Unlimited" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_kodure = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0xf000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_UNKNOWN );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_SERVICE( 0x0200, IP_ACTIVE_LOW );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_TILT );    /* Tilt */
		PORT_BIT( 0xf800, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x04, 0x04, "1st Bonus Life" );
		PORT_DIPSETTING(    0x04, "None" );
		PORT_DIPSETTING(    0x00, "50k" );
		PORT_DIPNAME( 0x08, 0x08, "2nd Bonus Life" );
		PORT_DIPSETTING(    0x08, "60k" );
		PORT_DIPSETTING(    0x00, "90k" );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x40, "Easy" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x00, DEF_STR( "3C_1C") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x10, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x40, 0x40, "Allow Continue" );
		PORT_DIPSETTING(    0x00, DEF_STR( "No") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Yes") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_cclimbr2 = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_PLAYER1 );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_START1 );
		PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_START2 );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_COIN1 );
		PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_COIN2 );
		PORT_BIT( 0xf000, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP     | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN   | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT   | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0010, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0020, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0040, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0080, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY | IPF_PLAYER2 );
		PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_SERVICE1 );
		PORT_SERVICE( 0x0200, IP_ACTIVE_LOW );
		PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_TILT );    /* Tilt */
		PORT_BIT( 0xf800, IP_ACTIVE_LOW, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x03, "3" );
		PORT_DIPSETTING(    0x02, "4" );
		PORT_DIPSETTING(    0x01, "5" );
		PORT_DIPSETTING(    0x00, "6" );
		PORT_DIPNAME( 0x04, 0x04, "First Bonus" );
		PORT_DIPSETTING(    0x04, "30k" );
		PORT_DIPSETTING(    0x00, "60k" );
		PORT_DIPNAME( 0x08, 0x08, "Second Bonus" );
		PORT_DIPSETTING(    0x08, "70k Only" );
		PORT_DIPSETTING(    0x00, "Nothing" );
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x40, "Easy" );
		PORT_DIPSETTING(    0x00, "Normal" );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x03, 0x03, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Free_Play") );
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "2C_3C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPNAME( 0x10, 0x10, "Continue Play" );
		PORT_DIPSETTING(    0x10, "3" );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( "Flip_Screen") );
		PORT_DIPSETTING(    0x20, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_BITX(    0x40, 0x40, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE );
		PORT_DIPSETTING(    0x40, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x80, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x00, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout char_layout = new GfxLayout
	(
		8,8,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 4, 0, 12, 8, 20, 16, 28, 24},
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32 },
		32*8
	);
	
	static GfxLayout tile_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,1),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 4, 0, 12, 8, 20, 16, 28, 24,
				32+4, 32+0, 32+12, 32+8, 32+20, 32+16, 32+28, 32+24, },
		new int[] { 0*64, 1*64, 2*64, 3*64, 4*64, 5*64, 6*64, 7*64,
				8*64, 9*64, 10*64, 11*64, 12*64, 13*64, 14*64, 15*64 },
		128*8
	);
	
	static GfxLayout sprite_layout = new GfxLayout
	(
		16,16,
		RGN_FRAC(1,2),
		4,
		new int[] { 0, 1, 2, 3 },
		new int[] { 4, 0, RGN_FRAC(1,2)+4, RGN_FRAC(1,2)+0, 12, 8, RGN_FRAC(1,2)+12, RGN_FRAC(1,2)+8,
				20, 16, RGN_FRAC(1,2)+20, RGN_FRAC(1,2)+16, 28, 24, RGN_FRAC(1,2)+28, RGN_FRAC(1,2)+24 },
		new int[] { 0*32, 1*32, 2*32, 3*32, 4*32, 5*32, 6*32, 7*32,
				8*32, 9*32, 10*32, 11*32, 12*32, 13*32, 14*32, 15*32 },
		64*8
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, char_layout,		 0*16,	32 ),
		new GfxDecodeInfo( REGION_GFX2, 0, tile_layout,		64*16,	32 ),
		new GfxDecodeInfo( REGION_GFX3, 0, tile_layout,		96*16,	32 ),
		new GfxDecodeInfo( REGION_GFX4, 0, sprite_layout,	32*16,	32 ),
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static YM3812interface ym3812_interface = new YM3812interface
	(
		1,				/* 1 chip (no more supported) */
		4000000,        /* 4 MHz */
		new int[] { 255 }         /* (not supported) */
	);
	
	public static InterruptPtr armedf_interrupt = new InterruptPtr() { public int handler() {
		return (1);
	} };
	
	public static InterruptPtr cclimbr2_interrupt = new InterruptPtr() { public int handler() {
		return (2);
	} };
	
	static DACinterface dac_interface = new DACinterface
	(
		2,	/* 2 channels */
		new int[] { 100,100 },
	);
	
	static DACinterface cclimbr2_dac_interface = new DACinterface
	(
		2,	/* 2 channels */
		new int[] { 40, 40 },
	);
	
	
	
	static MachineDriver machine_driver_terraf = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				8000000, /* 8 MHz?? */
				terraf_readmem,terraf_writemem,null,null,
				armedf_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3072000,	/* 3.072 MHz???? */
				soundreadmem,soundwritemem,readport,writeport,
				interrupt,128
			),
		},
		57, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 12*8, (64-12)*8-1, 1*8, 31*8-1 ),
		gfxdecodeinfo,
		2048,2048,
		0,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_BUFFERS_SPRITERAM,
		armedf_eof_callback,
		terraf_vh_start,
		null,
		armedf_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
			   SOUND_YM3812,
			   ym3812_interface
			),
			new MachineSound(
				SOUND_DAC,
				dac_interface
			)
		}
	);
	
	static MachineDriver machine_driver_kodure = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				8000000, /* 8 MHz?? */
				kodure_readmem, kodure_writemem, null, null,
				armedf_interrupt, 1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3072000,	/* 3.072 MHz???? */
				soundreadmem, soundwritemem, readport, writeport,
				interrupt, 128
			),
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 14*8, (64-14)*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		2048,2048,
		0,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_BUFFERS_SPRITERAM,
		armedf_eof_callback,
		kodure_vh_start,
		null,
		armedf_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
				SOUND_YM3812,
				ym3812_interface
			),
			new MachineSound(
				SOUND_DAC,
				dac_interface
			)
		}
	);
	
	static MachineDriver machine_driver_armedf = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				8000000, /* 8 MHz?? */
				armedf_readmem,armedf_writemem,null,null,
				armedf_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3072000,	/* 3.072 MHz???? */
				soundreadmem,soundwritemem,readport,writeport,
				interrupt,128
			),
		},
		57, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 12*8, (64-12)*8-1, 1*8, 31*8-1 ),
		gfxdecodeinfo,
		2048,2048,
		0,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_BUFFERS_SPRITERAM,
		armedf_eof_callback,
		armedf_vh_start,
		null,
		armedf_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
			   SOUND_YM3812,
			   ym3812_interface
			),
			new MachineSound(
				SOUND_DAC,
				dac_interface
			)
		}
	);
	
	static MachineDriver machine_driver_cclimbr2 = new MachineDriver
	(
		new MachineCPU[] {
			new MachineCPU(
				CPU_M68000,
				8000000, /* 8 MHz?? */
				cclimbr2_readmem,cclimbr2_writemem,null,null,
				cclimbr2_interrupt,1
			),
			new MachineCPU(
				CPU_Z80 | CPU_AUDIO_CPU,
				3072000,	/* 3.072 MHz???? */
				cclimbr2_soundreadmem,cclimbr2_soundwritemem,readport,writeport,
				interrupt,128
			),
		},
		60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
		1,
		null,
	
		/* video hardware */
		64*8, 32*8, new rectangle( 14*8, (64-14)*8-1, 2*8, 30*8-1 ),
		gfxdecodeinfo,
		2048,2048,
		0,
	
		VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE | VIDEO_BUFFERS_SPRITERAM,
		armedf_eof_callback,
		cclimbr2_vh_start,
		null,
		armedf_vh_screenrefresh,
	
		/* sound hardware */
		0,0,0,0,
		new MachineSound[] {
			new MachineSound(
			   SOUND_YM3812,
			   ym3812_interface
			),
			new MachineSound(
				SOUND_DAC,
				cclimbr2_dac_interface
			)
		}
	);
	
	
	
	static RomLoadPtr rom_terraf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000, REGION_CPU1 );/* 64K*8 for 68000 code */
		ROM_LOAD_EVEN( "terrafor.014", 0x00000, 0x10000, 0x8e5f557f );
		ROM_LOAD_ODD(  "terrafor.011", 0x00000, 0x10000, 0x5320162a );
		ROM_LOAD_EVEN( "terrafor.013", 0x20000, 0x10000, 0xa86951e0 );
		ROM_LOAD_ODD(  "terrafor.010", 0x20000, 0x10000, 0x58b5f43b );
		ROM_LOAD_EVEN( "terrafor.012", 0x40000, 0x08000, 0x4f0e1d76 );
		ROM_LOAD_ODD(  "terrafor.009", 0x40000, 0x08000, 0xd1014280 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code (sound) */
		ROM_LOAD( "terrafor.001", 0x00000, 0x10000, 0xeb6b4138 );
	
		ROM_REGION( 0x08000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.008", 0x00000, 0x08000, 0xbc6f7cbc );/* characters */
	
		ROM_REGION( 0x20000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.006", 0x00000, 0x10000, 0x25d23dfd );/* foreground tiles */
		ROM_LOAD( "terrafor.007", 0x10000, 0x10000, 0xb9b0fe27 );
	
		ROM_REGION( 0x20000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.004", 0x00000, 0x10000, 0x2144d8e0 );/* background tiles */
		ROM_LOAD( "terrafor.005", 0x10000, 0x10000, 0x744f5c9e );
	
		ROM_REGION( 0x20000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.003", 0x00000, 0x10000, 0xd74085a1 );/* sprites */
		ROM_LOAD( "terrafor.002", 0x10000, 0x10000, 0x148aa0c5 );
	
		ROM_REGION( 0x4000, REGION_GFX5 );/* data for mcu/blitter */
		ROM_LOAD( "tf.10",        0x0000, 0x4000, 0xac705812 );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "tf.clr",       0x0000, 0x0100, 0x81244757 );/* ??? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_terrafu = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x50000, REGION_CPU1 );/* 64K*8 for 68000 code */
		ROM_LOAD_EVEN( "tf.8",         0x00000, 0x10000, 0xfea6dd64 );
		ROM_LOAD_ODD(  "tf.3",         0x00000, 0x10000, 0x02f9d05a );
		ROM_LOAD_EVEN( "tf.7",         0x20000, 0x10000, 0xfde8de7e );
		ROM_LOAD_ODD(  "tf.2",         0x20000, 0x10000, 0xdb987414 );
		ROM_LOAD_EVEN( "tf.6",         0x40000, 0x08000, 0xb91e9ba3 );
		ROM_LOAD_ODD(  "tf.1",         0x40000, 0x08000, 0xd6e22375 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code (sound) */
		ROM_LOAD( "terrafor.001", 0x00000, 0x10000, 0xeb6b4138 );
	
		ROM_REGION( 0x08000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.008", 0x00000, 0x08000, 0xbc6f7cbc );/* characters */
	
		ROM_REGION( 0x20000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.006", 0x00000, 0x10000, 0x25d23dfd );/* foreground tiles */
		ROM_LOAD( "terrafor.007", 0x10000, 0x10000, 0xb9b0fe27 );
	
		ROM_REGION( 0x20000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.004", 0x00000, 0x10000, 0x2144d8e0 );/* background tiles */
		ROM_LOAD( "terrafor.005", 0x10000, 0x10000, 0x744f5c9e );
	
		ROM_REGION( 0x20000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "terrafor.003", 0x00000, 0x10000, 0xd74085a1 );/* sprites */
		ROM_LOAD( "terrafor.002", 0x10000, 0x10000, 0x148aa0c5 );
	
		ROM_REGION( 0x4000, REGION_GFX5 );/* data for mcu/blitter */
		ROM_LOAD( "tf.10",        0x0000, 0x4000, 0xac705812 );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "tf.clr",       0x0000, 0x0100, 0x81244757 );/* ??? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_kodure = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1 );/* 64K*8 for 68000 code */
		ROM_LOAD_EVEN( "kodure8.6e", 0x00000, 0x10000, 0x6bbfb1e6 );
		ROM_LOAD_ODD(  "kodure3.6h", 0x00000, 0x10000, 0xf9178ec8 );
		ROM_LOAD_EVEN( "kodure7.5e", 0x20000, 0x10000, 0xa7ee09bb );
		ROM_LOAD_ODD(  "kodure2.5h", 0x20000, 0x10000, 0x236d820f );
		ROM_LOAD_EVEN( "kodure6.3e", 0x40000, 0x10000, 0x9120e728 );
		ROM_LOAD_ODD(  "kodure1.3h", 0x40000, 0x10000, 0x345fe7a5 );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code (sound) */
		ROM_LOAD( "kodure11.17k", 0x00000, 0x10000, 0xdba51e2d );
	
		ROM_REGION( 0x08000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "kodure9.11e", 0x00000, 0x08000, 0xe041356e );/* characters */
	
		ROM_REGION( 0x40000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "kodure5.15h", 0x00000, 0x20000, 0x0b510258 );/* foreground tiles */
		ROM_LOAD( "kodure4.14h", 0x20000, 0x10000, 0xfb8e13e6 );
	
		ROM_REGION( 0x10000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "kodure14.8a", 0x00000, 0x10000, 0x94a9c3d0 );/* background tiles */
	
		ROM_REGION( 0x40000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "kodure12.8d", 0x00000, 0x20000, 0x15f4021d );/* sprites */
		ROM_LOAD( "kodure13.9d", 0x20000, 0x20000, 0xb3b6c753 );
	
		ROM_REGION( 0x4000, REGION_GFX5 );/* data for mcu/blitter */
		ROM_LOAD( "kodure10.11c", 0x0000, 0x4000, 0xf48be21d );
	
		ROM_REGION( 0x0100, REGION_PROMS );
		ROM_LOAD( "tf.11j", 0x0000, 0x0100, 0x81244757 );/* ??? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_cclimbr2 = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1 );/* 64K*8 for 68000 code */
		ROM_LOAD_EVEN( "4.bin", 0x00000, 0x10000, 0x7922ea14 );
		ROM_LOAD_ODD(  "1.bin", 0x00000, 0x10000, 0x2ac7ed67 );
		ROM_LOAD_EVEN( "6.bin", 0x20000, 0x10000, 0x7905c992 );
		ROM_LOAD_ODD(  "5.bin", 0x20000, 0x10000, 0x47be6c1e );
		ROM_LOAD_EVEN( "3.bin", 0x40000, 0x10000, 0x1fb110d6 );
		ROM_LOAD_ODD(  "2.bin", 0x40000, 0x10000, 0x0024c15b );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code (sound) */
		ROM_LOAD( "11.bin", 0x00000, 0x04000, 0xfe0175be );
		ROM_LOAD( "12.bin", 0x04000, 0x08000, 0x5ddf18f2 );
	
		ROM_REGION( 0x08000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "10.bin", 0x00000, 0x08000, 0x7f475266 );/* characters */
	
		ROM_REGION( 0x20000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "7.bin",  0x00000, 0x10000, 0xcbdd3906 );/* foreground tiles */
		ROM_LOAD( "8.bin",  0x10000, 0x10000, 0xb2a613c0 );
	
		ROM_REGION( 0x20000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "17.bin", 0x00000, 0x10000, 0xe24bb2d7 );/* background tiles */
		ROM_LOAD( "18.bin", 0x10000, 0x10000, 0x56834554 );
	
		ROM_REGION( 0x40000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "15.bin", 0x00000, 0x10000, 0x4bf838be );/* sprites */
		ROM_LOAD( "16.bin", 0x10000, 0x10000, 0x21a265c5 );
		ROM_LOAD( "13.bin", 0x20000, 0x10000, 0x6b6ec999 );
		ROM_LOAD( "14.bin", 0x30000, 0x10000, 0xf426a4ad );
	
		ROM_REGION( 0x4000, REGION_GFX5 );/* data for mcu/blitter */
		ROM_LOAD( "9.bin",  0x0000, 0x4000, 0x740d260f );
	ROM_END(); }}; 
	
	static RomLoadPtr rom_armedf = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x60000, REGION_CPU1 );/* 68000 code */
		ROM_LOAD_EVEN( "af_06.rom", 0x00000, 0x10000, 0xc5326603 );
		ROM_LOAD_ODD(  "af_01.rom", 0x00000, 0x10000, 0x458e9542 );
		ROM_LOAD_EVEN( "af_07.rom", 0x20000, 0x10000, 0xcc8517f5 );
		ROM_LOAD_ODD(  "af_02.rom", 0x20000, 0x10000, 0x214ef220 );
		ROM_LOAD_EVEN( "af_08.rom", 0x40000, 0x10000, 0xd1d43600 );
		ROM_LOAD_ODD(  "af_03.rom", 0x40000, 0x10000, 0xbbe1fe2d );
	
		ROM_REGION( 0x10000, REGION_CPU2 );/* Z80 code (sound) */
		ROM_LOAD( "af_10.rom", 0x00000, 0x10000, 0xc5eacb87 );
	
		ROM_REGION( 0x08000, REGION_GFX1 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "af_09.rom", 0x00000, 0x08000, 0x7025e92d );/* characters */
	
		ROM_REGION( 0x20000, REGION_GFX2 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "af_04.rom", 0x00000, 0x10000, 0x44d3af4f );/* foreground tiles */
		ROM_LOAD( "af_05.rom", 0x10000, 0x10000, 0x92076cab );
	
		ROM_REGION( 0x20000, REGION_GFX3 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "af_14.rom", 0x00000, 0x10000, 0x8c5dc5a7 );/* background tiles */
		ROM_LOAD( "af_13.rom", 0x10000, 0x10000, 0x136a58a3 );
	
		ROM_REGION( 0x40000, REGION_GFX4 | REGIONFLAG_DISPOSE );
		ROM_LOAD( "af_11.rom", 0x00000, 0x20000, 0xb46c473c );/* sprites */
		ROM_LOAD( "af_12.rom", 0x20000, 0x20000, 0x23cb6bfe );
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_terraf	   = new GameDriver("1987"	,"terraf"	,"armedf.java"	,rom_terraf,null	,machine_driver_terraf	,input_ports_terraf	,null	,ROT0	,	"Nichibutsu", "Terra Force", GAME_NO_COCKTAIL )
	public static GameDriver driver_terrafu	   = new GameDriver("1987"	,"terrafu"	,"armedf.java"	,rom_terrafu,driver_terraf	,machine_driver_terraf	,input_ports_terraf	,null	,ROT0	,	"Nichibutsu USA", "Terra Force (US)", GAME_NO_COCKTAIL )
	public static GameDriver driver_kodure	   = new GameDriver("1987"	,"kodure"	,"armedf.java"	,rom_kodure,null	,machine_driver_kodure	,input_ports_kodure	,null	,ROT0	,	"Nichibutsu", "Kodure Ookami (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_cclimbr2	   = new GameDriver("1988"	,"cclimbr2"	,"armedf.java"	,rom_cclimbr2,null	,machine_driver_cclimbr2	,input_ports_cclimbr2	,null	,ROT0	,	"Nichibutsu", "Crazy Climber 2 (Japan)", GAME_NO_COCKTAIL )
	public static GameDriver driver_armedf	   = new GameDriver("1988"	,"armedf"	,"armedf.java"	,rom_armedf,null	,machine_driver_armedf	,input_ports_armedf	,null	,ROT270	,	"Nichibutsu", "Armed Formation", GAME_NO_COCKTAIL )
}

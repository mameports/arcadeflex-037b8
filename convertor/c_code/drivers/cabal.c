/******************************************************************
Cabal Bootleg
(c)1998 Red Corp

driver by Carlos A. Lozano Baides

68000 + Z80

The original uses 2xYM3931 for sound
The bootleg uses YM2151 + 2xZ80 used as ADPCM players

MEMORY MAP
0x000000 - 0x03ffff   ROM
0x040000 - 0x0437ff   RAM
0x043800 - 0x0437ff   VRAM (Sprites)
0x060000 - 0x0607ff   VRAM (Tiles)
0x080000 - 0x0803ff   VRAM (Background)
0x0A0000 - 0xA0000f   Input Ports
0x0C0040 - 0x0c0040   Watchdog??
0x0C0080 - 0x0C0080   Watchdog??
0x0E0000 - 0x0E07ff   COLORRAM (----BBBBGGGGRRRR)
0x0E8000 - 0x0E800f   Output Ports/Input Ports

VRAM(Background)
0x80000 - 32 bytes (16 tiles)
0x80040 - 32 bytes (16 tiles)
0x80080 - 32 bytes (16 tiles)
0x800c0 - 32 bytes (16 tiles)
0x80100 - 32 bytes (16 tiles)
...
0x803c0 - 32 bytes (16 tiles)

VRAM(Tiles)
0x60000-0x607ff (1024 tiles 8x8 tiles, 2 bytes every tile)

VRAM(Sprites)
0x43800-0x43bff (128 sprites, 8 bytes every sprite)

COLORRAM(Colors)
0xe0000-0xe07ff (1024 colors, ----BBBBGGGGRRRR)

******************************************************************/

#include "driver.h"
#include "vidhrdw/generic.h"
#include "cpu/z80/z80.h"

static int cabal_sound_command1, cabal_sound_command2;

extern void cabal_vh_screenrefresh( struct osd_bitmap *bitmap, int fullrefresh );

static void cabal_init_machine( void ) {
	cabal_sound_command1 = cabal_sound_command2 = 0xff;
}

static WRITE16_HANDLER( cabal_background_w )
{
	int oldword = videoram16[offset];
	COMBINE_DATA(&videoram16[offset]);
	if (oldword != videoram16[offset])
		dirtybuffer[offset] = 1;
}

/******************************************************************************************/


static struct YM2151interface ym2151_interface =
{
 	1,			/* 1 chip */
 	3579580,	/* 3.58 MHz ? */ /* 4 MHz in raine */
	{ YM3012_VOL(80,MIXER_PAN_LEFT,80,MIXER_PAN_RIGHT) },
 	{ 0 }
};

struct ADPCMinterface adpcm_interface =
{
	2,			/* total number of ADPCM decoders in the machine */
	8000,		/* playback frequency */
	REGION_SOUND1, /* memory region where the samples come from */
	{40,40}
};

static void cabal_play_adpcm( int channel, int which ){
	if( which!=0xff ){
		unsigned char *RAM = memory_region(REGION_SOUND1);
		int offset = channel*0x10000;
		int start, len;

		which = which&0x7f;
		if( which ){
			which = which*2+0x100;
			start = RAM[offset+which] + 256*RAM[offset+which+1];
			len = (RAM[offset+start]*256 + RAM[offset+start+1])*2;
			start+=2;
			ADPCM_play( channel,offset+start,len );
		}
	}
}

static WRITE16_HANDLER( cabal_sndcmd_w )
{
	switch (offset) {
		case 0x0:
			cabal_sound_command1 = data;
			cpu_cause_interrupt( 1, Z80_NMI_INT );
		break;

		case 0x1: /* ?? */
			cabal_sound_command2 = data & 0xff;
		break;
	}
}

static MEMORY_READ16_START( readmem_cpu )
	{ 0x00000, 0x3ffff, MRA16_ROM },
	{ 0x40000, 0x437ff, MRA16_RAM },
	{ 0x43c00, 0x4ffff, MRA16_RAM },
	{ 0x43800, 0x43bff, MRA16_RAM },
	{ 0x60000, 0x607ff, MRA16_RAM },
	{ 0x80000, 0x801ff, MRA16_RAM },
	{ 0x80200, 0x803ff, MRA16_RAM },
	{ 0xa0000, 0xa0001, input_port_0_word_r },
	{ 0xa0008, 0xa0009, input_port_1_word_r },
	{ 0xa0010, 0xa0011, input_port_2_word_r },
	{ 0xe0000, 0xe07ff, MRA16_RAM },
	{ 0xe8004, 0xe8005, input_port_3_word_r },
MEMORY_END

static MEMORY_WRITE16_START( writemem_cpu )
	{ 0x00000, 0x3ffff, MWA16_ROM },
	{ 0x40000, 0x437ff, MWA16_RAM },
	{ 0x43800, 0x43bff, MWA16_RAM, &spriteram16, &spriteram_size },
	{ 0x43c00, 0x4ffff, MWA16_RAM },
	{ 0x60000, 0x607ff, MWA16_RAM, &colorram16 },
	{ 0x80000, 0x801ff, cabal_background_w, &videoram16, &videoram_size },
	{ 0x80200, 0x803ff, MWA16_RAM },
	{ 0xc0040, 0xc0041, MWA16_NOP }, /* ??? */
	{ 0xc0080, 0xc0081, MWA16_NOP }, /* ??? */
	{ 0xe0000, 0xe07ff, paletteram16_xxxxBBBBGGGGRRRR_word_w, &paletteram16 },
	{ 0xe8000, 0xe8003, cabal_sndcmd_w },
MEMORY_END

/*********************************************************************/

static READ_HANDLER( cabal_snd_r )
{
	switch(offset){
		case 0x08: return cabal_sound_command2;
		case 0x0a: return cabal_sound_command1;
		default: return(0xff);
	}
}

static WRITE_HANDLER( cabal_snd_w )
{
	switch( offset ){
		case 0x00: cabal_play_adpcm( 0, data ); break;
		case 0x02: cabal_play_adpcm( 1, data ); break;
     }
}

static MEMORY_READ_START( readmem_sound )
	{ 0x0000, 0x1fff, MRA_ROM },
	{ 0x2000, 0x2fff, MRA_RAM },
	{ 0x4000, 0x400d, cabal_snd_r },
	{ 0x400f, 0x400f, YM2151_status_port_0_r },
	{ 0x8000, 0xffff, MRA_ROM },
MEMORY_END

static MEMORY_WRITE_START( writemem_sound )
	{ 0x0000, 0x1fff, MWA_ROM },
	{ 0x2000, 0x2fff, MWA_RAM },
	{ 0x4000, 0x400d, cabal_snd_w },
	{ 0x400e, 0x400e, YM2151_register_port_0_w },
	{ 0x400f, 0x400f, YM2151_data_port_0_w },
	{ 0x6000, 0x6000, MWA_NOP },  /*???*/
	{ 0x8000, 0xffff, MWA_ROM },
MEMORY_END

static MEMORY_READ_START( cabalbl_readmem_sound )
	{ 0x0000, 0x1fff, MRA_ROM },
	{ 0x2000, 0x2fff, MRA_RAM },
	{ 0x4000, 0x400d, cabal_snd_r },
	{ 0x400f, 0x400f, YM2151_status_port_0_r },
	{ 0x8000, 0xffff, MRA_ROM },
MEMORY_END

static MEMORY_WRITE_START( cabalbl_writemem_sound )
	{ 0x0000, 0x1fff, MWA_ROM },
	{ 0x2000, 0x2fff, MWA_RAM },
	{ 0x4000, 0x400d, cabal_snd_w },
	{ 0x400e, 0x400e, YM2151_register_port_0_w },
	{ 0x400f, 0x400f, YM2151_data_port_0_w },
	{ 0x6000, 0x6000, MWA_NOP },  /*???*/
	{ 0x8000, 0xffff, MWA_ROM },
MEMORY_END

/* ADPCM CPU (common) */

#if 0
static MEMORY_READ_START( cabalbl_readmem_adpcm )
	{ 0x0000, 0xffff, MRA_ROM },
MEMORY_END
static MEMORY_WRITE_START( cabalbl_writemem_adpcm )
	{ 0x0000, 0xffff, MWA_NOP },
MEMORY_END
#endif


/***************************************************************************/

INPUT_PORTS_START( cabal )
	PORT_START
	PORT_DIPNAME( 0x000f, 0x000e, DEF_STR( Coinage ) )
	PORT_DIPSETTING(      0x000a, DEF_STR( 3C_1C ) )
	PORT_DIPSETTING(      0x000b, "3 Coins/1 Credit 5/2" )
	PORT_DIPSETTING(      0x000c, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(      0x000d, "2 Coins/1 Credit 3/2" )
	PORT_DIPSETTING(      0x0001, DEF_STR( 4C_3C ) )
	PORT_DIPSETTING(      0x000e, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(      0x0003, "2 Coins/2 Credits 3/4" )
	PORT_DIPSETTING(      0x0002, DEF_STR( 3C_3C ) )
	PORT_DIPSETTING(      0x000f, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(      0x0004, DEF_STR( 1C_3C ) )
	PORT_DIPSETTING(      0x0009, DEF_STR( 1C_4C ) )
	PORT_DIPSETTING(      0x0008, DEF_STR( 1C_6C ) )
	PORT_DIPSETTING(      0x0007, DEF_STR( 1C_8C ) )
	PORT_DIPSETTING(      0x0006, "1 Coin/10 Credits" )
	PORT_DIPSETTING(      0x0005, "1 Coin/12 Credits" )
	PORT_DIPSETTING(      0x0000, "Free 99 Credits" )
/* 0x10 is different from the Free 99 Credits.
   When you start the game the credits decrease using the Free 99,
   while they stay at 99 using the 0x10 dip. */
	PORT_DIPNAME( 0x0010, 0x0010, DEF_STR( Free_Play ) )
	PORT_DIPSETTING(      0x0010, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0020, 0x0020, "Invert Buttons" )
	PORT_DIPSETTING(      0x0020, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0040, 0x0040, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x0040, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x0080, 0x0080, "Trackball" )
	PORT_DIPSETTING(      0x0080, "Small" )
	PORT_DIPSETTING(      0x0000, "Large" )
	PORT_DIPNAME( 0x0300, 0x0300, DEF_STR( Lives ) )
	PORT_DIPSETTING(      0x0200, "2" )
	PORT_DIPSETTING(      0x0300, "3" )
	PORT_DIPSETTING(      0x0100, "5" )
	PORT_BITX( 0,         0x0000, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Infinite", IP_KEY_NONE, IP_JOY_NONE )
	PORT_DIPNAME( 0x0c00, 0x0c00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(      0x0c00, "20k 50k" )
	PORT_DIPSETTING(      0x0800, "30k 100k" )
	PORT_DIPSETTING(      0x0400, "50k 150k" )
	PORT_DIPSETTING(      0x0000, "70K" )
	PORT_DIPNAME( 0x3000, 0x3000, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(      0x3000, "Easy" )
	PORT_DIPSETTING(      0x2000, "Normal" )
	PORT_DIPSETTING(      0x1000, "Hard" )
	PORT_DIPSETTING(      0x0000, "Very Hard" )
	PORT_DIPNAME( 0x4000, 0x4000, DEF_STR( Unknown ) )
	PORT_DIPSETTING(      0x4000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( On ) )
	PORT_DIPNAME( 0x8000, 0x8000, DEF_STR( Demo_Sounds ) )
	PORT_DIPSETTING(      0x0000, DEF_STR( Off ) )
	PORT_DIPSETTING(      0x8000, DEF_STR( On ) )

	PORT_START	/* IN0 */
	PORT_BIT( 0x00ff, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0100, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0200, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0400, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x0800, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER1 )
	PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 )
	PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )

	PORT_START	/* IN1 */
	PORT_BIT( 0x0001, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER1 )
	PORT_BIT( 0x0002, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER1 )
	PORT_BIT( 0x0004, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
	PORT_BIT( 0x0008, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
	PORT_BIT( 0x00f0, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x0f00, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT( 0x1000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER2 )
	PORT_BIT( 0x2000, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_PLAYER1 )
	PORT_BIT( 0x4000, IP_ACTIVE_LOW, IPT_START2 )
	PORT_BIT( 0x8000, IP_ACTIVE_LOW, IPT_START1 )

	PORT_START	/* IN3 */
	PORT_BIT( 0x0003, IP_ACTIVE_LOW, IPT_UNKNOWN )
	PORT_BIT_IMPULSE( 0x0004, IP_ACTIVE_LOW, IPT_COIN1, 1 )
	PORT_BIT( 0xfff8, IP_ACTIVE_LOW, IPT_UNKNOWN )
INPUT_PORTS_END



static struct GfxLayout text_layout =
{
	8,8,	/* 8*8 characters */
	1024,	/* 1024 characters */
	2,	/* 4 bits per pixel */
	{ 0,4 },//0x4000*8+0, 0x4000*8+4, 0, 4 },
	{ 3, 2, 1, 0, 8+3, 8+2, 8+1, 8+0},
	{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16 },
	16*8	/* every char takes 16 consecutive bytes */
};

static struct GfxLayout tile_layout =
{
	16,16,  /* 16*16 tiles */
	4*1024,   /* 1024 tiles */
	4,      /* 4 bits per pixel */
	{ 0, 4, 0x40000*8+0, 0x40000*8+4 },
	{ 3, 2, 1, 0, 8+3, 8+2, 8+1, 8+0,
			32*8+3, 32*8+2, 32*8+1, 32*8+0, 33*8+3, 33*8+2, 33*8+1, 33*8+0 },
	{ 0*16, 1*16, 2*16, 3*16, 4*16, 5*16, 6*16, 7*16,
			8*16, 9*16, 10*16, 11*16, 12*16, 13*16, 14*16, 15*16 },
	64*8
};

static struct GfxLayout sprite_layout =
{
	16,16,	/* 16*16 sprites */
	4*1024,	/* 1024 sprites */
	4,	/* 4 bits per pixel */
	{ 0, 4, 0x40000*8+0, 0x40000*8+4 },	/* the two bitplanes for 4 pixels are packed into one byte */
	{ 3, 2, 1, 0, 8+3, 8+2, 8+1, 8+0,
			16+3, 16+2, 16+1, 16+0, 24+3, 24+2, 24+1, 24+0 },
	{ 30*16, 28*16, 26*16, 24*16, 22*16, 20*16, 18*16, 16*16,
			14*16, 12*16, 10*16,  8*16,  6*16,  4*16,  2*16,  0*16 },
	64*8	/* every sprite takes 64 consecutive bytes */
};

static struct GfxDecodeInfo cabal_gfxdecodeinfo[] = {
	{ REGION_GFX1, 0, &text_layout,		0, 1024/4 },
	{ REGION_GFX2, 0, &tile_layout,		32*16, 16 },
	{ REGION_GFX3, 0, &sprite_layout,	16*16, 16 },
	{ -1 }
};

static const struct MachineDriver machine_driver_cabal =
{
	{
		{
			CPU_M68000,
			12000000, /* 12 MHz */
			readmem_cpu,writemem_cpu,0,0,
			m68_level1_irq,1
		},
		{
			CPU_Z80 | CPU_AUDIO_CPU,
			4000000,	/* 4 MHz */
			readmem_sound,writemem_sound,0,0,
			interrupt,1
		},
	},
	60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
	1, /* CPU slices per frame */
	cabal_init_machine, /* init machine */

	/* video hardware */
	256, 256, { 0, 255, 16, 255-16 },

	cabal_gfxdecodeinfo,
	1024,1024,
	0,

	VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
	0,
	generic_vh_start,
	generic_vh_stop,
	cabal_vh_screenrefresh,

	/* sound hardware */
	0,0,0,0,
};

static const struct MachineDriver machine_driver_cabalbl =
{
	{
		{
			CPU_M68000,
			12000000, /* 12 MHz */
			readmem_cpu,writemem_cpu,0,0,
			m68_level1_irq,1
		},
		{
			CPU_Z80 | CPU_AUDIO_CPU,
			4000000,	/* 4 MHz */
			cabalbl_readmem_sound,cabalbl_writemem_sound,0,0,
			interrupt,1
		},
	},
	60, DEFAULT_REAL_60HZ_VBLANK_DURATION,
	10, /* CPU slices per frame */
	cabal_init_machine, /* init machine */

	/* video hardware */
	256, 256, { 0, 255, 16, 255-16 },

	cabal_gfxdecodeinfo,
	1024,1024,
	0,

	VIDEO_TYPE_RASTER | VIDEO_MODIFIES_PALETTE,
	0,
	generic_vh_start,
	generic_vh_stop,
	cabal_vh_screenrefresh,

	/* sound hardware */
	SOUND_SUPPORTS_STEREO,0,0,0,
	{
		{
			SOUND_YM2151,//_ALT,
			&ym2151_interface
		},
		{
			SOUND_ADPCM,
			&adpcm_interface
		}
	}
};

ROM_START( cabal )
	ROM_REGION( 0x50000, REGION_CPU1 )	/* 64k for cpu code */
	ROM_LOAD_EVEN( "h7_512.bin",      0x00000, 0x10000, 0x8fe16fb4 )
	ROM_LOAD_ODD ( "h6_512.bin",      0x00000, 0x10000, 0x6968101c )
	ROM_LOAD_EVEN( "k7_512.bin",      0x20000, 0x10000, 0x562031a2 )
	ROM_LOAD_ODD ( "k6_512.bin",      0x20000, 0x10000, 0x4fda2856 )

	ROM_REGION( 0x10000, REGION_CPU2 )	/* 64k for sound cpu code */
	ROM_LOAD( "4-3n",           0x0000, 0x2000, 0x4038eff2 )
	ROM_LOAD( "3-3p",           0x8000, 0x8000, 0xd9defcbf )

	ROM_REGION( 0x4000,  REGION_GFX1 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "t6_128.bin",     0x00000, 0x04000, 0x1ccee214 ) /* characters */

	/* the gfx ROMs were missing in this set, I'm using the ones from */
	/* the bootleg */
	ROM_REGION( 0x80000, REGION_GFX2 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "cabal_17.bin",   0x00000, 0x10000, 0x3b6d2b09 ) /* tiles, planes0,1 */
	ROM_LOAD( "cabal_16.bin",   0x10000, 0x10000, 0x77bc7a60 )
	ROM_LOAD( "cabal_18.bin",   0x20000, 0x10000, 0x0bc50075 )
	ROM_LOAD( "cabal_19.bin",   0x30000, 0x10000, 0x67e4fe47 )
	ROM_LOAD( "cabal_15.bin",   0x40000, 0x10000, 0x1023319b ) /* tiles, planes2,3 */
	ROM_LOAD( "cabal_14.bin",   0x50000, 0x10000, 0x420b0801 )
	ROM_LOAD( "cabal_12.bin",   0x60000, 0x10000, 0x543fcb37 )
	ROM_LOAD( "cabal_13.bin",   0x70000, 0x10000, 0xd28d921e )

	ROM_REGION( 0x80000, REGION_GFX3 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "cabal_05.bin",   0x00000, 0x10000, 0x4e49c28e ) /* sprites, planes0,1 */
	ROM_LOAD( "cabal_06.bin",   0x10000, 0x10000, 0x6a0e739d )
	ROM_LOAD( "cabal_07.bin",   0x20000, 0x10000, 0x581a50c1 )
	ROM_LOAD( "cabal_08.bin",   0x30000, 0x10000, 0x702735c9 )
	ROM_LOAD( "cabal_04.bin",   0x40000, 0x10000, 0x34d3cac8 ) /* sprites, planes2,3 */
	ROM_LOAD( "cabal_03.bin",   0x50000, 0x10000, 0x7065e840 )
	ROM_LOAD( "cabal_02.bin",   0x60000, 0x10000, 0x0e1ec30e )
	ROM_LOAD( "cabal_01.bin",   0x70000, 0x10000, 0x55c44764 )

	ROM_REGION( 0x20000, REGION_SOUND1 )	/* Samples? */
	ROM_LOAD( "2-1s",           0x00000, 0x10000, 0x850406b4 )
	ROM_LOAD( "1-1u",           0x10000, 0x10000, 0x8b3e0789 )
ROM_END

ROM_START( cabal2 )
	ROM_REGION( 0x50000, REGION_CPU1 )	/* 64k for cpu code */
	ROM_LOAD_EVEN( "9-7h",            0x00000, 0x10000, 0xebbb9484 )
	ROM_LOAD_ODD ( "7-6h",            0x00000, 0x10000, 0x51aeb49e )
	ROM_LOAD_EVEN( "8-7k",            0x20000, 0x10000, 0x4c24ed9a )
	ROM_LOAD_ODD ( "6-6k",            0x20000, 0x10000, 0x681620e8 )

	ROM_REGION( 0x10000, REGION_CPU2 )	/* 64k for sound cpu code */
	ROM_LOAD( "4-3n",           0x0000, 0x2000, 0x4038eff2 )
	ROM_LOAD( "3-3p",           0x8000, 0x8000, 0xd9defcbf )

	ROM_REGION( 0x4000,  REGION_GFX1 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "5-6s",           0x00000, 0x04000, 0x6a76955a ) /* characters */

	/* the gfx ROMs were missing in this set, I'm using the ones from */
	/* the bootleg */
	ROM_REGION( 0x80000, REGION_GFX2 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "cabal_17.bin",   0x00000, 0x10000, 0x3b6d2b09 ) /* tiles, planes0,1 */
	ROM_LOAD( "cabal_16.bin",   0x10000, 0x10000, 0x77bc7a60 )
	ROM_LOAD( "cabal_18.bin",   0x20000, 0x10000, 0x0bc50075 )
	ROM_LOAD( "cabal_19.bin",   0x30000, 0x10000, 0x67e4fe47 )
	ROM_LOAD( "cabal_15.bin",   0x40000, 0x10000, 0x1023319b ) /* tiles, planes2,3 */
	ROM_LOAD( "cabal_14.bin",   0x50000, 0x10000, 0x420b0801 )
	ROM_LOAD( "cabal_12.bin",   0x60000, 0x10000, 0x543fcb37 )
	ROM_LOAD( "cabal_13.bin",   0x70000, 0x10000, 0xd28d921e )

	ROM_REGION( 0x80000, REGION_GFX3 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "cabal_05.bin",   0x00000, 0x10000, 0x4e49c28e ) /* sprites, planes0,1 */
	ROM_LOAD( "cabal_06.bin",   0x10000, 0x10000, 0x6a0e739d )
	ROM_LOAD( "cabal_07.bin",   0x20000, 0x10000, 0x581a50c1 )
	ROM_LOAD( "cabal_08.bin",   0x30000, 0x10000, 0x702735c9 )
	ROM_LOAD( "cabal_04.bin",   0x40000, 0x10000, 0x34d3cac8 ) /* sprites, planes2,3 */
	ROM_LOAD( "cabal_03.bin",   0x50000, 0x10000, 0x7065e840 )
	ROM_LOAD( "cabal_02.bin",   0x60000, 0x10000, 0x0e1ec30e )
	ROM_LOAD( "cabal_01.bin",   0x70000, 0x10000, 0x55c44764 )

	ROM_REGION( 0x20000, REGION_SOUND1 )	/* Samples? */
	ROM_LOAD( "2-1s",           0x00000, 0x10000, 0x850406b4 )
	ROM_LOAD( "1-1u",           0x10000, 0x10000, 0x8b3e0789 )
ROM_END

ROM_START( cabalbl )
	ROM_REGION( 0x50000, REGION_CPU1 )	/* 64k for cpu code */
	ROM_LOAD_EVEN( "cabal_24.bin",    0x00000, 0x10000, 0x00abbe0c )
	ROM_LOAD_ODD ( "cabal_22.bin",    0x00000, 0x10000, 0x78c4af27 )
	ROM_LOAD_EVEN( "cabal_23.bin",    0x20000, 0x10000, 0xd763a47c )
	ROM_LOAD_ODD ( "cabal_21.bin",    0x20000, 0x10000, 0x96d5e8af )

	ROM_REGION( 0x10000, REGION_CPU2 )	/* 64k for sound cpu code */
	ROM_LOAD( "cabal_11.bin",    0x0000, 0x10000, 0xd308a543 )

	ROM_REGION( 0x4000,  REGION_GFX1 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "5-6s",           0x00000, 0x04000, 0x6a76955a ) /* characters */

	ROM_REGION( 0x80000, REGION_GFX2 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "cabal_17.bin",   0x00000, 0x10000, 0x3b6d2b09 ) /* tiles, planes0,1 */
	ROM_LOAD( "cabal_16.bin",   0x10000, 0x10000, 0x77bc7a60 )
	ROM_LOAD( "cabal_18.bin",   0x20000, 0x10000, 0x0bc50075 )
	ROM_LOAD( "cabal_19.bin",   0x30000, 0x10000, 0x67e4fe47 )
	ROM_LOAD( "cabal_15.bin",   0x40000, 0x10000, 0x1023319b ) /* tiles, planes2,3 */
	ROM_LOAD( "cabal_14.bin",   0x50000, 0x10000, 0x420b0801 )
	ROM_LOAD( "cabal_12.bin",   0x60000, 0x10000, 0x543fcb37 )
	ROM_LOAD( "cabal_13.bin",   0x70000, 0x10000, 0xd28d921e )

	ROM_REGION( 0x80000, REGION_GFX3 | REGIONFLAG_DISPOSE )
	ROM_LOAD( "cabal_05.bin",   0x00000, 0x10000, 0x4e49c28e ) /* sprites, planes0,1 */
	ROM_LOAD( "cabal_06.bin",   0x10000, 0x10000, 0x6a0e739d )
	ROM_LOAD( "cabal_07.bin",   0x20000, 0x10000, 0x581a50c1 )
	ROM_LOAD( "cabal_08.bin",   0x30000, 0x10000, 0x702735c9 )
	ROM_LOAD( "cabal_04.bin",   0x40000, 0x10000, 0x34d3cac8 ) /* sprites, planes2,3 */
	ROM_LOAD( "cabal_03.bin",   0x50000, 0x10000, 0x7065e840 )
	ROM_LOAD( "cabal_02.bin",   0x60000, 0x10000, 0x0e1ec30e )
	ROM_LOAD( "cabal_01.bin",   0x70000, 0x10000, 0x55c44764 )

	ROM_REGION( 0x20000, REGION_SOUND1 )
	ROM_LOAD( "cabal_09.bin",   0x00000, 0x10000, 0x4ffa7fe3 ) /* Z80 code/adpcm data */
	ROM_LOAD( "cabal_10.bin",   0x10000, 0x10000, 0x958789b6 ) /* Z80 code/adpcm data */
ROM_END



GAMEX( 1988, cabal,   0,     cabal,   cabal, 0, ROT0, "Tad (Fabtek license)", "Cabal (US set 1)", GAME_NOT_WORKING | GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
GAMEX( 1988, cabal2,  cabal, cabal,   cabal, 0, ROT0, "Tad (Fabtek license)", "Cabal (US set 2)", GAME_NOT_WORKING | GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )
GAMEX( 1988, cabalbl, cabal, cabalbl, cabal, 0, ROT0, "bootleg", "Cabal (bootleg)", GAME_IMPERFECT_SOUND | GAME_NO_COCKTAIL )

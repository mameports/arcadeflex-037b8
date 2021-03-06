/***************************************************************************

	Seibu Sound System v1.02, games using this include:

		Raiden
		Dynamite Duke/The Double Dynamites
		Blood Brothers
		D-Con
		Zero Team
		Legionaire (YM2151 substituted for YM3812)
		Raiden 2 (YM2151 substituted for YM3812, plus extra MSM6205)
		Raiden DX (YM2151 substituted for YM3812, plus extra MSM6205)
		Cup Soccer (YM2151 substituted for YM3812, plus extra MSM6205)

	Related sound programs (not implemented yet):
		Dead Angle
		Cabal

***************************************************************************/

void seibu_ym3812_irqhandler(int linestate);
WRITE16_HANDLER( seibu_soundlatch_word_w );
void install_seibu_sound_speedup(int cpu);

extern UBytePtr seibu_shared_sound_ram;

/**************************************************************************/

#define SEIBU_SOUND_SYSTEM_YM3812_MEMORY_MAP(input_port)			\
																	\
public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),						\
	new Memory_ReadAddress( 0x0000, 0x1fff, MRA_ROM ),									\
	new Memory_ReadAddress( 0x2000, 0x27ff, MRA_RAM ),									\
	new Memory_ReadAddress( 0x4008, 0x4008, YM3812_status_port_0_r ),						\
	new Memory_ReadAddress( 0x4010, 0x4012, seibu_soundlatch_r ), 						\
	new Memory_ReadAddress( 0x4013, 0x4013, input_port ), 								\
	new Memory_ReadAddress( 0x6000, 0x6000, OKIM6295_status_0_r ),						\
	new Memory_ReadAddress( 0x8000, 0xffff, MRA_BANK1 ),									\
	new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};														\
				  													\
public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),						\
	new Memory_WriteAddress( 0x0000, 0x1fff, MWA_ROM ),									\
	new Memory_WriteAddress( 0x2000, 0x27ff, MWA_RAM ),									\
	new Memory_WriteAddress( 0x4000, 0x4000, seibu_soundclear_w ),							\
	new Memory_WriteAddress( 0x4002, 0x4002, seibu_rst10_ack_w ), 							\
	new Memory_WriteAddress( 0x4003, 0x4003, seibu_rst18_ack_w ), 							\
	new Memory_WriteAddress( 0x4007, 0x4007, seibu_bank_w ),								\
	new Memory_WriteAddress( 0x4008, 0x4008, YM3812_control_port_0_w ),					\
	new Memory_WriteAddress( 0x4009, 0x4009, YM3812_write_port_0_w ),						\
	new Memory_WriteAddress( 0x4018, 0x401f, seibu_main_data_w ),							\
	new Memory_WriteAddress( 0x6000, 0x6000, OKIM6295_data_0_w ),							\
	new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};



#define SEIBU_SOUND_SYSTEM_YM3812_HARDWARE(freq1,freq2,region)		\
																	\
static YM3812interface ym3812_interface = new YM3812interface\
(																	\
	1,																\
	freq1,															\
	new int[] { 50 },															\
	new WriteYmHandlerPtr[] { seibu_ym3812_irqhandler },									\
);																	\
																	\
static OKIM6295interface okim6295_interface = new OKIM6295interface\
(																	\
	1,																\
	new int[] { freq2 },														\
	new int[] { region },														\
	new int[] { 40 }															\
)

#define SEIBU_SOUND_SYSTEM_CPU(freq)								\
	CPU_Z80 | CPU_AUDIO_CPU,										\
	freq,															\
	sound_readmem,sound_writemem,0,0,								\
	ignore_interrupt,0

#define SEIBU_SOUND_SYSTEM_YM3812_INTERFACE							\
	{																\
		SOUND_YM3812,												\
		&ym3812_interface											\
	},																\
	{																\
		SOUND_OKIM6295,												\
		&okim6295_interface											\
	}

/**************************************************************************/


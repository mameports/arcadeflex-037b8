#ifndef M68KMAME__HEADER
#define M68KMAME__HEADER

/* ======================================================================== */
/* ============================== MAME STUFF ============================== */
/* ======================================================================== */

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package cpu.m68000;

public class m68kmameH
{
	
	/* Configuration switches (see m68kconf.h for explanation) */
	#define M68K_SEPARATE_READ_IMM      OPT_ON
	
	#define M68K_EMULATE_INT_ACK        OPT_ON
	#define M68K_INT_ACK_CALLBACK(A)
	
	#define M68K_EMULATE_BKPT_ACK       OPT_OFF
	#define M68K_BKPT_ACK_CALLBACK()
	
	#define M68K_EMULATE_TRACE          OPT_OFF
	
	#define M68K_EMULATE_RESET          OPT_OFF
	#define M68K_RESET_CALLBACK()
	
	#define M68K_EMULATE_FC             OPT_OFF
	#define M68K_SET_FC_CALLBACK(A)
	
	#define M68K_MONITOR_PC             OPT_SPECIFY_HANDLER
	#define M68K_SET_PC_CALLBACK(A)     change_pc32bew(A)
	
	#define M68K_INSTRUCTION_HOOK       OPT_SPECIFY_HANDLER
	#define M68K_INSTRUCTION_CALLBACK() CALL_MAME_DEBUG
	
	#define M68K_EMULATE_PREFETCH       OPT_ON
	
	#define M68K_LOG_ENABLE             OPT_OFF
	#define M68K_LOG_1010_1111          OPT_OFF
	#define M68K_LOG_FILEHANDLE         errorlog
	
	#define M68K_EMULATE_ADDRESS_ERROR  OPT_OFF
	
	#define M68K_USE_64_BIT             OPT_OFF
	
	
	/* Redirect memory calls */
	#define m68k_read_memory_8(address)          cpu_readmem32bew(address)
	#define m68k_read_memory_16(address)         cpu_readmem32bew_word(address)
	INLINE unsigned int m68k_read_memory_32(unsigned int address)
	{
		return (cpu_readmem32bew_word(address)<<16) | cpu_readmem32bew_word((address)+2);
	}
	
	#define m68k_read_immediate_16(address)      cpu_readop_arg16(address)
	INLINE unsigned int m68k_read_immediate_32(unsigned int address)
	{
		return (cpu_readop_arg16(address)<<16) | cpu_readop_arg16((address)+2);
	}
	
	#define m68k_read_disassembler_8(address)    cpu_readmem32bew(address)
	#define m68k_read_disassembler_16(address)   cpu_readmem32bew_word(address)
	INLINE unsigned int m68k_read_disassembler_32(unsigned int address)
	{
		return (cpu_readmem32bew_word(address)<<16) | cpu_readmem32bew_word((address)+2);
	}
	
	
	#define m68k_write_memory_8(address, value)  cpu_writemem32bew(address, value)
	#define m68k_write_memory_16(address, value) cpu_writemem32bew_word(address, value)
	INLINE void m68k_write_memory_32(unsigned int address, unsigned int value)
	{
		cpu_writemem32bew_word(address, value >> 16);
		cpu_writemem32bew_word(address+2, value & 0xffff);
	}
	
	
	/* Redirect ICount */
	#define m68000_ICount m68ki_remaining_cycles
	
	
	/* M68K Variants */
	#if HAS_M68010
	#define M68K_EMULATE_010            OPT_ON
	#else
	#define M68K_EMULATE_010            OPT_OFF
	#endif
	
	#if HAS_M68EC020
	#define M68K_EMULATE_EC020          OPT_ON
	#else
	#define M68K_EMULATE_EC020          OPT_OFF
	#endif
	
	#if HAS_M68020
	#define M68K_EMULATE_020            OPT_ON
	#else
	#define M68K_EMULATE_020            OPT_OFF
	#endif
	
	/* ======================================================================== */
	/* ============================== END OF FILE ============================= */
	/* ======================================================================== */
	
	#endif /* M68KMAME__HEADER */
}

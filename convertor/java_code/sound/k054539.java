/*********************************************************

	Konami 054539 PCM Sound Chip

*********************************************************/

/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package sound;

public class k054539
{
	
	#define MAX_K054539 2
	
	static struct {
		const struct K054539interface	*intf;
		UBytePtr rom;
		int								rom_size;
		struct {
			unsigned char regs[0x22f];
			UBytePtr ram;
			int cur_ptr;
			int cur_limit;
			UBytePtr cur_zone;
			void *timer;
		} chip[MAX_K054539];
	} K054539_chips;
	
	static void K054539_irq(int chip)
	{
		if(K054539_chips.chip[chip].regs[0x22f] & 0x20)
			K054539_chips.intf.irq[chip] ();
	}
	
	static void K054539_init_chip(int chip)
	{
		memset(K054539_chips.chip[chip].regs, 0, sizeof(K054539_chips.chip[chip].regs));
		K054539_chips.chip[chip].ram = malloc(0x4000);
		K054539_chips.chip[chip].cur_ptr = 0;
		if(K054539_chips.intf.irq[chip])
			// One or more of the registers must be the timer period
			// And anyway, this particular frequency is probably wrong
			K054539_chips.chip[chip].timer = timer_pulse(TIME_IN_HZ(500), 0, K054539_irq);
		else
			K054539_chips.chip[chip].timer = 0;
	
	}
	
	static void K054539_stop_chip(int chip)
	{
		free(K054539_chips.chip[chip].ram);
		if (K054539_chips.chip[chip].timer)
			timer_remove(K054539_chips.chip[chip].timer);
	}
	
	static void K054539_w(int chip, offs_t offset, data8_t data)
	{
		K054539_chips.chip[chip].regs[offset] = data;
		switch(offset) {
		case 0x22d:
			if(K054539_chips.chip[chip].regs[0x22e] == 0x80)
				K054539_chips.chip[chip].cur_zone[K054539_chips.chip[chip].cur_ptr] = data;
			K054539_chips.chip[chip].cur_ptr++;
			if(K054539_chips.chip[chip].cur_ptr == K054539_chips.chip[chip].cur_limit)
				K054539_chips.chip[chip].cur_ptr = 0;
			break;
		case 0x22e:
			K054539_chips.chip[chip].cur_zone =
				data == 0x80 ? K054539_chips.chip[chip].ram :
					K054539_chips.rom + 0x20000*data;
			K054539_chips.chip[chip].cur_limit = data == 0x80 ? 0x4000 : 0x20000;
			K054539_chips.chip[chip].cur_ptr = 0;
			break;
		}
	}
	
	static data8_t K054539_r(int chip, offs_t offset)
	{
		switch(offset) {
		case 0x22d:
			if(K054539_chips.chip[chip].regs[0x22f] & 0x10) {
				data8_t res = K054539_chips.chip[chip].cur_zone[K054539_chips.chip[chip].cur_ptr];
				K054539_chips.chip[chip].cur_ptr++;
				if(K054539_chips.chip[chip].cur_ptr == K054539_chips.chip[chip].cur_limit)
					K054539_chips.chip[chip].cur_ptr = 0;
				return res;
			} else
				return 0;
		}
		return K054539_chips.chip[chip].regs[offset];
	}
	
	public static ShStartPtr K054539_sh_start = new ShStartPtr() { public int handler(MachineSound msound) 
	{
		int i;
	
		K054539_chips.intf = msound.sound_interface;
		K054539_chips.rom = memory_region(K054539_chips.intf.region);
		K054539_chips.rom_size = memory_region_length(K054539_chips.intf.region);
	
		for(i=0; i<K054539_chips.intf.num; i++)
			K054539_init_chip(i);
		return 0;
	} };
	
	public static ShStopPtr K054539_sh_stop = new ShStopPtr() { public void handler() 
	{
		int i;
		for(i=0; i<K054539_chips.intf.num; i++)
			K054539_stop_chip(i);
	} };
	
	public static WriteHandlerPtr K054539_0_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		K054539_w(0, offset, data);
	} };
	
	public static ReadHandlerPtr K054539_0_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return K054539_r(0, offset);
	} };
	
	public static WriteHandlerPtr K054539_1_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		K054539_w(1, offset, data);
	} };
	
	public static ReadHandlerPtr K054539_1_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return K054539_r(1, offset);
	} };
}

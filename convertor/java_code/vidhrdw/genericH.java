/*
 * ported to v0.37b8
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class genericH
{
	
	#ifdef __cplusplus
	extern "C" {
	#endif
	
	extern data8_t *videoram;
	extern data16_t *videoram16;
	extern data32_t *videoram32;
	extern size_t videoram_size;
	extern data8_t *colorram;
	extern data16_t *colorram16;
	extern data32_t *colorram32;
	extern data8_t *spriteram;
	extern data16_t *spriteram16;
	extern data32_t *spriteram32;
	extern data8_t *spriteram_2;
	extern data16_t *spriteram16_2;
	extern data32_t *spriteram32_2;
	extern data8_t *spriteram_3;
	extern data16_t *spriteram16_3;
	extern data32_t *spriteram32_3;
	extern data8_t *buffered_spriteram;
	extern data16_t *buffered_spriteram16;
	extern data32_t *buffered_spriteram32;
	extern data8_t *buffered_spriteram_2;
	extern data16_t *buffered_spriteram16_2;
	extern data32_t *buffered_spriteram32_2;
	extern size_t spriteram_size;
	extern size_t spriteram_2_size;
	extern size_t spriteram_3_size;
	extern data8_t *dirtybuffer;
	extern data16_t *dirtybuffer16;
	extern data32_t *dirtybuffer32;
	extern struct osd_bitmap *tmpbitmap;
	
	
	WRITE16_HANDLER( buffer_spriteram16_w );
	WRITE32_HANDLER( buffer_spriteram32_w );
	WRITE16_HANDLER( buffer_spriteram16_2_w );
	WRITE32_HANDLER( buffer_spriteram32_2_w );
	void buffer_spriteram(UBytePtr ptr,int length);
	void buffer_spriteram_2(UBytePtr ptr,int length);
	
	#ifdef __cplusplus
	}
	#endif
}

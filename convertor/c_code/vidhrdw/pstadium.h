void pstadium_vh_screenrefresh(struct osd_bitmap *bitmap, int full_refresh);
void galkoku_vh_screenrefresh(struct osd_bitmap *bitmap, int full_refresh);
int pstadium_vh_start(void);
void pstadium_vh_stop(void);

READ_HANDLER( pstadium_palette_r );
WRITE_HANDLER( pstadium_palette_w );
WRITE_HANDLER( galkoku_palette_w );
WRITE_HANDLER( galkaika_palette_w );
void pstadium_radrx_w(int data);
void pstadium_radry_w(int data);
void pstadium_sizex_w(int data);
void pstadium_sizey_w(int data);
void pstadium_dispflag_w(int data);
void pstadium_drawx_w(int data);
void pstadium_drawy_w(int data);
void pstadium_scrollx_w(int data);
void pstadium_scrolly_w(int data);
void pstadium_gfxflag_w(int data);
void pstadium_romsel_w(int data);
void pstadium_paltblnum_w(int data);
READ_HANDLER( pstadium_paltbl_r );
WRITE_HANDLER( pstadium_paltbl_w );

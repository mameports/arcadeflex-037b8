int TC0480SCP_vh_start(int gfxnum,int pixels,int x_offset,int y_offset,int col_base);
READ16_HANDLER( TC0480SCP_word_r );
WRITE16_HANDLER( TC0480SCP_word_w );
READ16_HANDLER( TC0480SCP_ctrl_word_r );
WRITE16_HANDLER( TC0480SCP_ctrl_word_w );
void TC0480SCP_tilemap_draw(struct osd_bitmap *bitmap,int layer,int flags,UINT32 priority);


int TC0100SCN_vh_start(int chips,int gfxnum,int x_offset);
READ16_HANDLER( TC0100SCN_word_0_r );
WRITE16_HANDLER( TC0100SCN_word_0_w );
READ16_HANDLER( TC0100SCN_ctrl_word_0_r );
WRITE16_HANDLER( TC0100SCN_ctrl_word_0_w );
READ16_HANDLER( TC0100SCN_word_1_r );
WRITE16_HANDLER( TC0100SCN_word_1_w );
READ16_HANDLER( TC0100SCN_ctrl_word_1_r );
WRITE16_HANDLER( TC0100SCN_ctrl_word_1_w );
void TC0100SCN_tilemap_draw(struct osd_bitmap *bitmap,int chip,int layer,int flags,UINT32 priority);
/* returns 0 or 1 depending on the lowest priority tilemap set in the internal
   register. Use this function to draw tilemaps in the correct order. */
int TC0100SCN_bottomlayer(int chip);


int TC0280GRD_vh_start(int gfxnum);
READ16_HANDLER( TC0280GRD_word_r );
WRITE16_HANDLER( TC0280GRD_word_w );
WRITE16_HANDLER( TC0280GRD_ctrl_word_w );
void TC0280GRD_tilemap_update(int base_color);
void TC0280GRD_zoom_draw(struct osd_bitmap *bitmap,int xoffset,int yoffset,UINT32 priority);

int TC0430GRW_vh_start(int gfxnum);
READ16_HANDLER( TC0430GRW_word_r );
WRITE16_HANDLER( TC0430GRW_word_w );
WRITE16_HANDLER( TC0430GRW_ctrl_word_w );
void TC0430GRW_tilemap_update(int base_color);
void TC0430GRW_zoom_draw(struct osd_bitmap *bitmap,int xoffset,int yoffset,UINT32 priority);


READ16_HANDLER( TC0110PCR_word_r );
WRITE16_HANDLER( TC0110PCR_word_w );




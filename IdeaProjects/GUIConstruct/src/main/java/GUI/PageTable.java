package GUI;

public class PageTable {
    //given
    private int PAGE_WIDTH;
    private int PAGE_HEIGHT;
    private int PAGE_OFFSET_X;
    private int PAGE_OFFSET_Y;
    private int total_entries; //sensible value
    private int total_pages; //sensible value

    //private int[][] total_page_entryArray;

    //derived
    private int entries_per_page;
    private int box_width;
    private int max_horizontal_boxes;
    private int max_vertical_boxes;


    private int active_page = 0;

    /**
     * All positive integers above 0
     * @param max_width
     * @param max_height
     * @param entries
     * @param pages
     * @param x x position of the page (from top left corner)
     * @param y y position of the page (from top left corner)
     */
    public PageTable(int x, int y, int max_width, int max_height, int entries, int pages){
        PAGE_OFFSET_X = x;
        PAGE_OFFSET_Y = y;
        PAGE_WIDTH = max_width;
        PAGE_HEIGHT = max_height;
        total_pages = pages;
        total_entries = entries;
        entries_per_page = Math.ceilDiv(entries,pages);
        double box_width_d = Math.sqrt(((double)(max_width)*(double)(max_height))/(double)entries_per_page);
        max_horizontal_boxes = (int)Math.ceil(max_width/box_width_d);
        max_vertical_boxes = (int)Math.ceil(max_height/box_width_d);
        box_width = (int)box_width_d;
        for(int i = 0; i < 2; i++) { //2 iterations of box width correction, when either box row or columns protrude the bounds of the table PAGE_WIDTH or PAGE_HEIGHT
            if ((max_vertical_boxes * box_width - max_height) - (max_horizontal_boxes * box_width - max_width) < 0) {
                //System.out.println("w: " + w + " " + max_horizontal_boxes * box_width);
                box_width = max_width / max_horizontal_boxes;
                //System.out.println("w: " + w + " " + max_horizontal_boxes * box_width);
            } else {
                //System.out.println("h: " + h + " " + max_vertical_boxes * box_width);
                box_width = max_height / max_vertical_boxes;
                //System.out.println("h: " + h + " " + max_vertical_boxes * box_width);
            }
        }
        //total_page_entryArray = new int[pages][(int)Math.ceil((double)entries/pages)];
        /***
         * once table is initialized, total pages and entries can not be arbitrarily modified
         */
    }

    public int getAbsoluteEntry(int page, int entry_on_page){
        if(page < 0 || page >= total_pages){
            return -1;
        }
        if(entry_on_page < 0 || entry_on_page >= entries_per_page){
            return -1;
        }
        return page*entries_per_page + entry_on_page;
    }

    public int selectEntry(int mouseX, int mouseY){
        int lX = mouseX - PAGE_OFFSET_X;
        int lY = mouseY - PAGE_OFFSET_Y;
        if(lX < 0 || lY < 0 || lX >= PAGE_WIDTH || lY >= PAGE_HEIGHT){
            return -1;
        }
        int box_X = lX/box_width; //0 to PAGE_WIDTH/box_width
        int box_Y = lY/box_width; //0 to PAGE_HEIGHT/box_width(also box_width <==> box height)

        int entry_on_page = box_Y*max_horizontal_boxes + box_X;
        if(entry_on_page >= entries_per_page){
            return -1;
        }
        int absolute_entry = this.getActivePage()*entries_per_page + entry_on_page;
        if(absolute_entry >= total_entries){
            return -1;
        }
        return absolute_entry;
    }


    /**
     * @param page positive integer >= 0 and < total_pages
     */
    public void setPage(int page){
        if(page < 0 || page >= total_pages){
            return;
        }
        active_page = page;
    }

    /**
     * next page (specify if page exceeds max, set page to 0)
     */
    public void incrementPage(boolean loop){
        if(active_page + 1 < total_pages) {
            active_page++;
        }else if(loop){
            active_page = 0;
        }
    }

    /**
     * next page (specify if page precedes 0, set page to max)
     */
    public void decrementPage(boolean loop){
        if(active_page - 1 >= 0){
            active_page--;
        }else if(loop){
            active_page = total_pages - 1;
        }
    }

    /**
     * set Position offset of the table in the pane
     */
    public void setOffset(int x, int y){
        PAGE_OFFSET_X = x;
        PAGE_OFFSET_Y = y;
    }

    public void setTableDimensions(int w, int h){
        PAGE_WIDTH = w;
        PAGE_HEIGHT = h;
        double box_width_d = Math.sqrt(((double)(w)*(double)(h))/(double)entries_per_page);
        max_horizontal_boxes = (int)Math.ceil(w/box_width_d);
        max_vertical_boxes = (int)Math.ceil(h/box_width_d);
        box_width = (int)box_width_d;
        //box_width = ((max_vertical_boxes*box_width - h) - (max_horizontal_boxes*box_width - w) > 0) ? (w/max_horizontal_boxes) : (h/max_vertical_boxes);
        for(int i = 0; i < 2; i++) {
            if ((max_vertical_boxes * box_width - h) - (max_horizontal_boxes * box_width - w) < 0) {
                //System.out.println("w: " + w + " " + max_horizontal_boxes * box_width);
                box_width = w / max_horizontal_boxes;
                //System.out.println("w: " + w + " " + max_horizontal_boxes * box_width);
            } else {
                //System.out.println("h: " + h + " " + max_vertical_boxes * box_width);
                box_width = h / max_vertical_boxes;
                //System.out.println("h: " + h + " " + max_vertical_boxes * box_width);
            }
        }
    }

    public void addEntry() {
        total_entries++;
        if (entries_per_page * total_pages < total_entries) {
            total_pages++;
        }
    }

    public void removeEntry(){
        if(total_entries >= 1) {
            total_entries--;
        }
        if(entries_per_page*(total_pages - 1) >= total_entries){ //*point of possible critical miscalculation
            total_pages--;
        }
    }
    public int getActivePage(){
        return active_page;
    }
    public int getEntriesPerPage(){
        return entries_per_page;
    }

    public int getXOffset(){
        return PAGE_OFFSET_X;
    }

    public int getYOffset(){
        return PAGE_OFFSET_Y;
    }

    /**
     * iterate over 0 to entries_per_page - 1
     * @param entry_on_page
     * @return int[3] {x position, y position, absolute entry}
     */
    public int[] getBoxOnActivePage(int entry_on_page){
        int absolute_entry = active_page*entries_per_page + entry_on_page;
        if(absolute_entry >= total_entries || entry_on_page >= entries_per_page){
            return null;
        }

        return new int[]{(int)((entry_on_page%(int)max_horizontal_boxes)*box_width + PAGE_OFFSET_X), (int)((entry_on_page/(int)max_horizontal_boxes)*box_width + PAGE_OFFSET_Y), absolute_entry};
    }
    public int getBoxWidth(){
        return box_width;
    }

    public int getWidth(){
        return PAGE_WIDTH;
    }
    public int getHeight(){
        return PAGE_HEIGHT;
    }
}

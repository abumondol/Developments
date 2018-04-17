function segs = our_find_segments_xth(g, th)
    gx = g(:,1);    
    g_count = length(gx);
    
    segs = [];
    six = 0;    
    for i=1:g_count
        if gx(i,1)<th && six > 0 
            segs = [segs; six, i-1];
            six = 0;
        elseif gx(i,1)>=th && six ==0                    
            six = i;
        end                
    end            

    if six>0
        segs = [segs; six, g_count];
    end    

end
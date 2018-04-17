function bp = our_find_bite_points(gx, segs)
    th = 3*30;
    bp = [];    
    if isempty(segs)        
        return
    end
    
    seg_count = size(segs, 1);     
    for i=1:seg_count
        s= segs(i,1);
        e = segs(i, 2);        
        
        res = find_bite_points_one_segment(gx, s, e, th);
        
        if isempty(bp)
            bp = [bp;res];
            continue;
        end
        
        
        if res(1,1) - bp(end, 1)<th
            if bp(end,2) < res(1,2)
                if size(bp,1)>1
                    bp = bp(1:end-1, :);
                else
                    bp = [];
                end            
            else
                if size(res,1)>1
                    res = res(2:end, :);
                else
                    res = [];
                end            
            end
        end
        
        bp = [bp; res];                    
    end        
    
    
    bp = bp(:,1);
end

function bpo = find_bite_points_one_segment(gx, s, e, th)
    if s==e
        bpo = [s, gx(s,1)];
        return;
    end
    
    if e-s<th
        [m, I] = max(gx(s:e, 1));
        a = s + I -1;
        b = m;
        bpo = [a, b];        
        return;
    end
    
    mid = round((s+e)/2);
    lp = find_bite_points_one_segment(gx, s, mid, th);
    rp = find_bite_points_one_segment(gx, mid, e, th);
    
    if rp(1,1) - lp(end, 1)<th
        if lp(end,2) < rp(1,2)
            if size(lp,1)>1
                lp = lp(1:end-1, :);
            else
                lp = [];
            end            
        else
            if size(rp,1)>1
                rp = rp(2:end, :);
            else
                rp = [];
            end            
        end
    end
    bpo = [lp;rp];    
end
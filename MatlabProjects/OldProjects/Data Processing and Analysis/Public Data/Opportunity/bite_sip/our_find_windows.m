function res = our_find_windows(g, bp, cos_theta, max_len)
    bp_count = size(bp,1);
    g_count = size(g,1);
    res = zeros(bp_count, 2);
    
    for i=1:bp_count
        ix = bp(i,1);
        p = g(ix, :);
        
        ix_left = ix;        
        ix_right = ix;
        left = true;
        right = true;
        
        mlen = 0;        
        while mlen <= max_len && (left || right)
            
            if left && ix_left>1
                ctheta = sum(p.*g(ix_left,:), 2);
                if ctheta >= cos_theta
                    ix_left = ix_left - 1;                    
                    mlen = mlen + 1;
                else
                    left = false;
                end
            else
                left = false;
            end
            
            if right && ix_right<g_count
                ctheta = sum(p.*g(ix_right,:), 2);
                if ctheta >= cos_theta
                    ix_right = ix_right + 1;                    
                    mlen = mlen + 1;
                else
                    right = false;
                end
            else
                right = false;
            end
        end
        
        res(i,:) = [ix_left, ix_right];        
    end

end
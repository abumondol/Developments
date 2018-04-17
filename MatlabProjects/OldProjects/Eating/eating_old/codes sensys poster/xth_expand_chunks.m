function chunks = xth_expand_chunks(accel, chunks)
    t = accel(:,1);
    a = accel(:, 2:4);    
    chunk_count = size(chunks,1);    
    x = a(:,1);
    acl_count = length(x);
    
    for i = 1:chunk_count
        s = chunks(i, 1);
        e = chunks(i, 2);
        
        while s>1 && x(s) < x(s-1) && x(s) <=0
            s = s-1;
        end
                
        while e < acl_count && x(e) < x(e+1) && x(e) <=0
            e = e+1;            
        end
                
        if e == acl_count
            chunks(end,:) = [];
            return;
        end
        
        if x(s) > x(e)
            while x(s) > x(e)
                s = s+1;
            end
        elseif x(e) > x(s)
            while x(e) > x(s)
                e = e-1;
            end
        end        
       
        chunks(i,1:4) = [s, e, t(s), t(e)];
        
    end

end
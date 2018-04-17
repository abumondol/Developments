function windows = get_windows_by_chunks(a, t, chunks)
    chunk_count = length(chunks);
    windows = zeros(chunk_count, 6);
    x = a(:,1);
    acl_count = length(x);
    
    for i = 1:chunk_count
        s = chunks(i, 2);
        e = chunks(i, 3);
        
        while x(s) <= x(s-1) && x(s) <=0
            s = s-1;
        end
        
        while e < acl_count && x(e) <= x(e+1) && x(e) <=0
            e = e+1;            
        end
        
        if e == acl_count
            windows(end,:) = [];
            return;
        end
            
        windows(i,:) = [chunks(i,1), s, e, t(s), t(e), t(e) - t(s)];        
    end

end
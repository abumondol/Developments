function segments = find_segments(data, min_th, min_length, max_length, step_length)    
    t = data(:,1);
    x = data(:,2);
    len = length(x);    
    %slide_length = round(max_length/2);    
    segments = [];            
    
    th = min_th;
    i = 1;    
    while i < len        
        
        while x(i) > min_th && i < len
            i = i + 1;
            th = min_th;
        end
        
        while i < len && x(i) <= x(i+1)
            i = i + 1;
        end
        
        if i == len
            break;
        end
        
        if x(i) > min_th
            continue
        end
        
        a = i;
        th = x(a);        
        end_time = t(a) + max_length;
        
        while t(i) < end_time && i < len && x(i) <= th
            i = i + 1;
        end
        
        if i==len
            break
        elseif x(i) > th && t(i-1) - t(a) >= min_length
            segments = [segments; a, i-1, th];
        else
            th = th - step_length;
            i = a;
            while x(i) > th && i<len
                i = i+1;
            end
    
        end
    end
    
end


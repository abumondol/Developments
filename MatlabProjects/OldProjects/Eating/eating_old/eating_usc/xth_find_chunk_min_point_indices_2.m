function selected = xth_find_chunk_min_point_indices_2(data, min_th, min_length, max_length)
    t = data(:,1);
    x = data(:,2);
    len = length(x);
    min_indices = [];    
    
    i = 1;
    while i < len        
        th = min_th;
        while x(i) > th && i < len
            i = i + 1;
        end
        
        if i == len
            break;
        end
        
        a = i;
        th = x(i)
        end_time = t(i) + max_length;
        while t(i) < end_time && i < len && x(i) <= th
            i = i + 1;
        end
        
        if i==b
            i = a+1;        
        elseif i-a >= min_length
            [M, I] = min(x(a:i));
            min_indices = [min_indices; a+I-1];  
        end        
    end
    
    if min_distance == 0
        selected = min_indices;
        return;
    end
    
    count = length(min_indices);
    selected = [];
    i = 1;
    while i < count
        if min_indices(i+1) - min_indices(i) > min_distance
            selected = [selected;min_indices(i)];
            i = i+1;
        else
            if x(min_indices(i)) < x(min_indices(i+1))
                selected = [selected;min_indices(i)];
                i = i + 2;            
            else
                i = i + 1;
            end
        end
    end
    
end


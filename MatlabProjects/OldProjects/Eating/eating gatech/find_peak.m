function [min_index, left, right] = find_peak(x, start, w, x_th)
    d = x(start:start+w-1);
    [mn, min_index] = min(d);
    min_index = start + min_index -1;
    if mn > x_th
        p = 0;
        left = 0;
        right = 0;
        return
    end       
    
    
    [mx_left, max_index_left] = max(x(min_index-w/2+1:min_index-1));
    [mx_right, max_index_right] = max(x(min_index:min_index+w/2));
    
    left = min_index;
    while abs(mn - x(left)) < abs(mx_left - x(left)) && x(left)< 0
        left = left -1;
    end
    
    while x(left)< 0 && x(left-1) > x(left)
        left = left -1;
    end
    
    
    right = min_index;
    while abs(mn - x(right)) < abs(mx_right - x(right)) && x(right)< 0
        right = right + 1;
    end
    
    while x(right)< 0 && x(right+1) > x(right)
        right = right + 1;
    end
end
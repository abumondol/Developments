function [windows, total] = find_windows(data, min_th, min_length, window_length)    
    x = data(:,2);
    len = length(x);    
    slide_length = round(window_length/2);    
    windows = [];            
    total = 0;
    
    for i = 1:slide_length:len-window_length
        total = total + 1;        
        if check_window(x(i:i+window_length-1), min_th, min_length)
            windows = [windows; i, i+window_length-1];
        end        
    end
    
end

function res = check_window(d, min_th, min_length)
    res = false;
    if min(d) >= min_th        
        return
    end
    
    len = length(d);
    i = 1;
    while i < len
        while d(i) > min_th && i<len
            i = i + 1;
        end        
        a = i;
        
        while d(i) <= min_th && i<len
            i = i + 1;
        end
        b = i;
        
        if b-a >=min_length
            res = true;
            return
        end
    end   

end

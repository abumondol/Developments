function events = find_events(res)    
    events = [];
    len = length(res);
    si = 0;
    if res(1) == 1
        si = 1;
    end
    for i=2:len
       if res(i) == 1 && res(i-1) == 0
            si = i;
        elseif res(i) == 0 && res(i-1) == 1
            events = [events; si, i-1];
        end 
    end
        
end
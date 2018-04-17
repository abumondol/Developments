function weight = find_axis_weight(pairs, grid)
    weight = zeros(8,1);
    total_pairs = size(pairs,1);    
    stability = zeros(8,1);
    
    d = zeros(total_pairs,1);
    for axis = 1:8        
        for j=1:total_pairs
            a = pairs(j,1);
            b = pairs(j,2);
            d(j,1) = grid(a,b).dist(axis);
        end
        
        stability(axis,1) = 1/std(d); 
    end
    
    s = sum(stability);
    weight =  stability/s;   
    
end
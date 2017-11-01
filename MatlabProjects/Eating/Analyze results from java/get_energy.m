function e = get_energy(a, w)
    a = a(2:end, :) - a(1:end-1, :);
    a = [a(1, :); a];
    a = abs(a);
    a = sum(a, 2);
    
    e = a;
    count = length(a);
    for i=1:w
        e(i) = mean(a(1:i+w));
    end
    
    for i=w+1:count-w
        e(i) = mean(a(i-w:i+w));
    end
    
    for i=count-w+1:count
        e(i) = mean(a(i-w:count));
    end
    
end
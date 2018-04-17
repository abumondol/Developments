function [left, right] = get_angular_boundary(d, ix, theta)
    len = length(d);
    a = d(ix, :);
    
    cos_theta = cosd(theta);
    i = ix;
    while i<len        
        b = d(i, :);
        v = a(1)*b(1) + a(2)*b(2) + a(3)*b(3);
        if v<cos_theta
            break
        end
        i = i+1;
    end
    
    right = i;
    if right>ix+150
        right = ix +100;
    end 
    
    i = ix;
    while i>1        
        b = d(i, :);
        v = a(1)*b(1) + a(2)*b(2) + a(3)*b(3);
        if v<cos_theta
            break
        end
        i = i-1;
    end    
    
    left = i;    
    if left<ix-150
        left = ix -100;
        if left<1
            left = 1;
        end
    end 
    
end
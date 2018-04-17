function a = get_smaller(a)

    theta = sum(a(1, :).*a(2, :));
    while theta < 0.99
        theta
        res = [];
        len = size(a, 1);
        for i=1:len-1            
            mid = (a(i, :) + a(i+1,:))/2;
            mag = sum(mid.*mid);
            mid = mid./[mag, mag, mag];
            res = [res;a(i, :);mid];
        end
        res = [res;a(len, :)];
        a = res;        
        theta = sum(a(1, :).*a(2, :));
    end    
    
end


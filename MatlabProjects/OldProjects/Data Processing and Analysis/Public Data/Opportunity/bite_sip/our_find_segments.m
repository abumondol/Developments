function segs = find_segments(g, mu, sigma, k)

    x = abs(g(:,1)-mu(1)) <= k*sigma(1);
    y = abs(g(:,2)-mu(2)) <= k*sigma(2);
    z = abs(g(:,3)-mu(3)) <= k*sigma(3);
    a = x & y & z;

    g_count = size(g,1);
    segs = [];
    six = a(1);
    for i=2:g_count
        if a(i)==0 && six > 0 
            segs = [segs; six, i-1];
            six = 0;
        elseif a(i)==1 && six ==0                    
            six = i;
        end                
    end            

    if six>0
        segs = [segs; six, g_count];
    end

end
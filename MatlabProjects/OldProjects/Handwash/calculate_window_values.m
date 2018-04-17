res= [];
for sess=1:1
    st = data(sess).subject(1).accel(1,1);
    et = data(sess).subject(1).accel(end,1);

    count = length(st:1000:et);
    res1 = zeros(count, 5);
    res2 = zeros(count, 5);
    events = zeros(count, 1);
    
    a1 = data(sess).subject(1).accel;
    a2 = data(sess).subject(2).accel;
    b1 = data(sess).subject(1).beacon;
    b2 = data(sess).subject(2).beacon;
    ev = data(sess).events(:,1);

    
    a1(:,2:4) = mysmooth(a1(:,2:4), 0.8);
    a2(:,2:4) = mysmooth(a2(:,2:4), 0.8);
    
    ad1 = a1(2:end, 2:4) - a1(1:end-1, 2:4);
    ad2 = a2(2:end, 2:4) - a2(1:end-1, 2:4);
    ad1 = [ad1(1,:); ad1];
    ad2 = [ad2(1,:); ad2];
    a1 = [a1, ad1];
    a2 = [a2, ad2];


    i = 1;
    for t = st:1000:et
        
        a = a1;
        a = a(a(:,1)>=t & a(:,1)<t+1000, :);       
        if ~isempty(a)
            res1(i, 1:3) = [mean(a(:,2)), sum(std(a(:, 2:4),1)), sum(rms(a(:, 5:7), 1))];
        end
        
        a = b1;
        a = a(a(:,1)>=t & a(:,1)<t+1000, :);
        if ~isempty(a)
            res1(i, 4) = mean(a(:,3));
            m = max(a(:,4));
            res1(i, 5) = m;
        end
        
        a = a2;
        a = a(a(:,1)>=t & a(:,1)<t+1000, :);
        if ~isempty(a)
            res2(i, 1:3) = [mean(a(:,2)), sum(std(a(:, 2:4),1)), sum(rms(a(:, 5:7), 1))];
        end
        
        a = b2;
        a = a(a(:,1)>=t & a(:,1)<t+1000, :);
        if ~isempty(a)
            res2(i, 4) = mean(a(:,3));
            m = max(a(:,4));
            res2(i, 5) = m;
        end
        
        a = ev;
        a = a(a(:,1)>=t & a(:,1)<t+1000, :);
        if ~isempty(a)
            events(i,1) = 1;
        end
        
        i = i+1;
        
    end
    
    res(sess).res1 = res1;
    res(sess).res2 = res2;
    res(sess).events = events;    

end


function q = quantize_data(data)
    data = round(100*data);
    len = length(data);    
    q = zeros(len, 3);
    
    j = 1;
    a = data(1,1);
    b = data(1,2);
    count = 1;
    for i =2:len
        if data(i,1) == a && data(i, 2) == b
            count = count+1;
        else
            q(j,:) = [a, b, count];
            j = j + 1;
            a = data(i, 1);
            b = data(i, 2);
            count = 1;
        end            
    end    
    q(j,:) = [a, b, count];
    q = q(1:j, :);        
end
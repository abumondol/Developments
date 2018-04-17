sub_count = length(data)

rates = zeros(sub_count, 1);

for s =1:sub_count      
    t = data(s).accel(:, 1);    
    x_count = length(t);
    rates(s) = x_count/(t(end)-t(1));   
end
rates

function peak_points = find_peak_points(data)
peak_points = [];

len = length(data);
for i = 2:len-1
    if  (data(i) > data(i-1) && data(i) > data(i+1))  || (data(i) < data(i-1) && data(i) < data(i+1))
        peak_points = [peak_points; i data(i)];
    end
end
end
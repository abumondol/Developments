sub_count = length(data);

for s =1:sub_count
    fprintf('Subject %d\n', s);
    t = data(s).accel(:, 1);
    d = data(s).accel(:, 2:4);
    m = sqrt(sum(d.*d,2));
    d = d./[m, m, m];
    %distances = 1 - sum(d(2:end, :).*d(1:end-1, :), 2);
    %distances = [0; distances];
    %data(s).accel_norm = [t, d, distances];
    data(s).accel_norm = d;
end

save('data', 'data');


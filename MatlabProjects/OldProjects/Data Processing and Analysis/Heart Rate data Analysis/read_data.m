subjects = {'abu1', 'abu2', 'anindya', 'jashim', 'saikat', 'wasi'};

data = [];
for i=1:6
    filename = strcat('src/',subjects{i},'.wada')
    d = csvread(filename);    
    data(i).start_time = d(1,1)
    hr = d(d(:,2)==21, :);
    hr(:,1) = hr(:,1) - hr(1,1);
    hr(:,1) = hr(:,1)/1e9;    
    data(i).hr = hr(:, 1:4);    
end

save('data', 'data');
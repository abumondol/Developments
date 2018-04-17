folder = 'C:/Users/mm5gg/Box Sync/Data Sets/Hand wash/Hand Wash Mobiquitous/sensys_ds1/mobisys_sub_';
poses = {'1', '2', '3', '4', '5', '6', '7', 'mix_hw', 'not_hw'};
data = [];
for s =1:9
    for p=1:9
        filename = strcat(folder, num2str(s),'_right_', poses{p})
        d = csvread(filename);
        mag = sqrt(sum(d(:, 10:12).*d(:, 10:12), 2));
        mag = repmat(mag, 1, 3);
        d = [d, d(:, 10:12)./mag];
        data(s).hand(1).session(1).pose(p).data = d;
        filename = strcat(folder, num2str(s),'_left_', poses{p})
        d = csvread(filename);
        mag = sqrt(sum(d(:, 10:12).*d(:, 10:12), 2));
        mag = repmat(mag, 1, 3);
        d = [d, d(:, 10:12)./mag];
        data(s).hand(2).session(1).pose(p).data = d;        
    end 
end

ds1 = data;
save('ds1', 'ds1');

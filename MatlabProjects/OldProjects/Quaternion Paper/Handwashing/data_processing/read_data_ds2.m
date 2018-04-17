folder = 'C:/Users/mm5gg/Box Sync/Data Sets/Hand wash/Hand Wash Mobiquitous/sensys_ds2/sub_';
poses = {'1', '2_1', '2_2','3','4', '5_1', '5_2', '6_1', '6_2', '7', 'mix_hw', 'not_hw'};
data = [];
for s =1:7
    for sess = 1:5
        for p=1:12
            filename = strcat(folder, num2str(s),'_right_', num2str(sess), '_pose_', poses{p})
            d = csvread(filename);
            mag = sqrt(sum(d(:, 10:12).*d(:, 10:12), 2));
            mag = repmat(mag, 1, 3);
            d = [d, d(:, 10:12)./mag];
            data(s).hand(1).session(sess).pose(p).data = d;
            
            filename = strcat(folder, num2str(s),'_left_', num2str(sess), '_pose_', poses{p})
            d = csvread(filename);
            mag = sqrt(sum(d(:, 10:12).*d(:, 10:12), 2));
            mag = repmat(mag, 1, 3);
            d = [d, d(:, 10:12)./mag];
            data(s).hand(2).session(sess).pose(p).data = d;                    
        end 
    end
end

ds2 = data;
save('ds2', 'ds2');

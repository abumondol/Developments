sub = 34;
a = csvread(strcat('res_csv/left/res_left_', num2str(sub)));
b = csvread(strcat('segment_data/test_left/left_', num2str(sub)));
b = b(:, end);
res_left = [b ,a];

a = csvread(strcat('res_csv/right/res_right_', num2str(sub)));
b = csvread(strcat('segment_data/test_right/right_', num2str(sub)));
b = b(:, end);
res_right = [b ,a];

count = length(accel);
step = 180*16;
for i=1:step:count    
    close all
    %figure
    figure('units','normalized','outerposition',[0 0 1 1]);
    r = res_left(res_left(:,1)>=i & res_left(:,1)< i+step, :);
    if ~isempty(r)
        scatter(r(:,1), r(:,2), 'rx');
        xlim([i, i+step]);
        ylim([-1.1, 1.1]);
        hold on        
        
        r = res_right(res_right(:,1)>=i & res_right(:,1)< i+step, :);
        scatter(r(:,1), r(:,2), 'go');
    end    
    
    waitforbuttonpress
end



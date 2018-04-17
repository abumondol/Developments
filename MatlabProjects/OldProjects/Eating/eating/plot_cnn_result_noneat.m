sub = 39;
a = csvread(strcat('res/res_', num2str(sub)));
b = csvread(strcat('segment_data/test/', num2str(sub)));
b = b(:, end);
res = [b ,a];

accel = data(sub).accel;
annots = data(sub).annots;

count = length(accel);
step = 600*16;
for i=1:step:count    
    close all
    %figure
    figure('units','normalized','outerposition',[0 0 1 1]);
    r = res(res(:,1)>=i & res(:,1)< i+step, :);
    if ~isempty(r)
        scatter(r(:,1), r(:,2), 'rx');
        xlim([i, i+step]);
        ylim([0, 1.1]);    
    end    
    waitforbuttonpress
end



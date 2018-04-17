sub = 11;
a = csvread(strcat('res/res_', num2str(sub)));
b = csvread(strcat('segment_data/test/', num2str(sub)));
b = b(:, end);
res = [b ,a];

accel = data(sub).accel;
annots = data(sub).annots;
ai = get_annot_indices(accel, annots);
bi = ai(ai(:,2)<400, :);
di = ai(ai(:,2)>=400 & ai(:,2)<1000,  :);
ni = ai(ai(:,2)>=1000,  :);

bi(:,2) =1;
di(:,2) = 1;
ni(:,2) = 1;

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
        hold on        
    end
    
        
    b = bi(bi(:,1)>=i & bi(:,1)< i+step, :);
    if ~isempty(b)        
        scatter(b(:,1), b(:,2), 'bo');
        hold on
    end
    
    d = di(di(:,1)>=i & di(:,1)< i+step, :);
    if ~isempty(d)
        scatter(d(:,1), d(:,2),  'go');
        hold on
    end
    
    n = ni(ni(:,1)>=i & ni(:,1)< i+step, :);
    if ~isempty(n)
        scatter(n(:,1), n(:,2), 'yo');
    end
    
    waitforbuttonpress
end



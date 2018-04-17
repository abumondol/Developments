load('data_usc');
load('segments_usc');
offset = 10;

for sub = 2:2    
    data = data_usc(sub).grav;
    data(:,2:4) = mysmooth(data(:, 2:4), 0.8);
    segments = segments_usc(sub).segments;
    
    seg_count = size(segments, 1);
    for i = 1:seg_count
        a= segments(i,1);
        b = segments(i,2);
        offset = 10*64 -(b-a);
        offset = floor(offset/2);        
        close all
        figure
        ind =a-offset:b+offset; 
        plot(data(ind,1), data(ind,2));
        hold on;
        plot([data(a-offset,1);data(b+offset,1)], [0, 0]);
        scatter([data(a,1); data(b,1)], [data(a,2);data(b,2)]);
        title(num2str(data(b,1) - data(a,1)));
        waitforbuttonpress
        
    end    
                
end



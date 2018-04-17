%load('data')
offset = 16*10;

for sub = 1:8    
    d = data(sub).accel_norm;    
    mp = segments(sub).boundaries;
        
    annot_count = size(mp, 1);
    for i = 1:annot_count
        close all;
        figure('units','normalized','outerposition',[0 0 1 1]);        
        p = mp(i, 1);
        
        x = p-offset:p+offset;
        y = d(x, 2:4);                                    
        plot(x', y);    
        ylim([-1, 1]);
        legend('X','Y','Z');        
        hold on
        grid on
        plot([p-offset;p+offset], [0; 0], 'k'); %plot zero line                        
        
        scatter(p, 0, 'go'); % annotated mark point
        scatter([p-mp(i,2); p+mp(i,3)], [0; 0], 'rx'); % annotated boundary points
                
        waitforbuttonpress        
    end
                
end



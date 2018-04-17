%load('data')
offset = 16*20;

for sub = 1:8    
    d = data(sub).accel;
    ap = data(sub).annots_adjusted;
    ap = get_annot_indices(d, ap);
    mp = segments(sub).min_points(:,1);
    accel_count = length(d);    
    
    annot_count = size(annots, 1);
    for i = 1:offset:accel_count        
        close all;
        figure('units','normalized','outerposition',[0 0 1 1]);        
        
        x = i:i+offset;
        y = d(x, 2:4);                                    
        plot(x', y);    
        ylim([-10, 10]);
        legend('X','Y','Z');        
        hold on
        grid on
        plot([i;i+offset], [0; 0], 'k'); %plot zero line                        
        
        a = mp(mp(:,1)>=i & mp(:,1)<= i+offset, 1);
        if ~isempty(a)            
            scatter(a, zeros(length(a), 1), 'rx'); % annotated mark point
        end
        
        a = ap(ap(:,1)>=i & ap(:,1)<= i+offset, 1);
        if ~isempty(a)            
            scatter(a, zeros(length(a), 1), 'go'); % annotated mark point
        end
        
        waitforbuttonpress        
    end
                
end



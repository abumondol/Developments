
offset = 5;
for s=1:1%12
    d = data(s).data;
    a = data(s).annots;
    a = a(a(:,3)<=7, :);
    annot_count = size(a, 1);
    Rz = d(:, 2:4);
    len = length(Rz)
    
    for i = 1:1%annot_count
        ix = a(i,1)
        t = a(i,2);
        label = a(i,3);
        if i == 5
            break
        end
        
        close all;        
        figure
        t = t + 0.5
        d1 = d(d(:,1) >= t-offset & d(:,1) <= t+offset-2, :);          

        ttl = '';                    
        x = d1(:,1); % timestamps
        x = x - t+offset;
        y = d1(:,2);        
        plot(x, y); 
        ylim([-1, 1]);
        xlim([x(1), x(end)]);
        xlabel('Time (second)');
        ylabel('g_x');
        
        %title(ttl);
        hold on
        %grid on
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line
        for p = 0:10
            plot([p;p], [-1; +1], 'k'); %plot zero line
        end
        %scatter(0, 0, 'rd'); % annotated mark point        
        %waitforbuttonpress 
        
        x = [1.176;4.376];
        y = [-0.3605;-0.4963];
        scatter(x, y, 'ro');
        x = 4.376;
        y = -0.4671;
        %scatter(x, y, 'ro'); % annotated mark point        
       
    end
    
end
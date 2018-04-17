load('data');
offset = 2;

for sub = 1:5
    for sess=1:2
        d1 = data(sub).session(sess).grav;                
        
        annots = data(sub).session(sess).annotations;
        annots = annots(annots(:,2)==1, :);        
        
        annot_count = size(annots, 1);
        for i = 1:annot_count
            s = annots(i,1);
            label = annots(i,2);
            
            close all;
            %figure('units','normalized','outerposition',[0 0 1 1]);
            figure            
            d = d1;
            d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, :);                                    
            
            x = d(:,1); % timestamps
            x = x - x(1);
            y = d(:,2:4); %x, y, z axes val
            
            plot(x, y, 'LineWidth',4);
            h_legend=legend('X','Y','Z');
            set(h_legend,'FontSize',32);
            
             a = get(gca,'XTickLabel');
            set(gca,'XTickLabel',a,'fontsize',16)
            
            %title(ttl);
            xlabel('Time (sec)', 'FontSize', 32);
            ylabel('Accelration (m/s^2)', 'FontSize', 32);
            hold on
            plot([x(1);x(end)], [0; 0], 'k', 'LineWidth',4); %plot zero line                        
            %scatter(offset, 0, 'rd'); % annotated mark point  
            
           

           
            waitforbuttonpress            
        end
                
    end
end


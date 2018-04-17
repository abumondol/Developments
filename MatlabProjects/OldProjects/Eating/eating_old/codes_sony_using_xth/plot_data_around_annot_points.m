load('procdata');
offset = 5;

for sub = 8:8
    sess_count = length(procdata(sub).session);
    for sess = 1:sess_count
        d1 = procdata(sub).session(sess).grav;        
        d2 = procdata(sub).session(sess).gyro;        
        d1 = d1(1:4:end,:);
        d2 = d2(1:4:end, :);
        %d2 = d1(2:end, 2:4) - d1(1:end-1, 2:4);
        %d2 = [d2(1,:);d2];
        %d2 = [d1(:,1), d2];
        
        annots = procdata(sub).session(sess).annotations;
        annots = annots(annots(:,2)<1000, :);
        
        %d1(:,2:4) = smooth(d1(:,2:4), 0.8);
        %d2(:,2:4) = smooth(d2(:,2:4), 0.8);        

        annot_count = size(annots, 1)
        for i = 1:annot_count
            s = annots(i,1);
            label = annots(i,2);
            
            close all;
            figure('units','normalized','outerposition',[0 0 1 1]);
            
            d = d1;
            d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, :);            
            subplot(2,1,1)       % add first plot in 2 x 1 grid
            
            ttl = strcat('sub:', num2str(sub), ', sess:', num2str(sess), ', annot no:', num2str(i), ', time:', time2str(s), ', label:', num2str(label) );            
            x = d(:,1); % timestamps
            x = x - x(1);
            y = d(:,2:4); %x, y, z axes val
            
            plot(x, y);
            legend('X','Y','Z');            
            title(ttl);
            hold on
            grid on
            plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
            scatter(offset, 0, 'rd'); % annotated mark point
            
            d = d2;
            d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, :);            
            subplot(2,1,2)       % add second plot in 2 x 1 grid            
            x = d(:,1); % timestamps
            x = x - x(1);
            y = d(:,2:4); %x, y, z axes val
            
            plot(x, y);
            legend('X','Y','Z');                        
            hold on
            grid on
            plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
            scatter(offset, 0, 'rd'); % annotated mark point          
           
            waitforbuttonpress            
        end
                
    end
end


load('data_usc');
data = data_usc;
offset = 20;

for sub = 2:2    
    d1 = data(sub).grav;
    rate = length(d1)/(d1(end,1)-d1(1,1))
    d1(:,2:4) = smooth(d1(:,2:4), 0.90);        
    
    d2 = diff(d1(:, 2:4));       
    d2 = [d1(:,1), [d2; d2(end,:)]];
    %d2(:,2:4) = smooth(d2(:,2:4), 0.90); 

    annots = data(sub).annotations;
    %annots = annots(annots(:,2)==102, :);                
    %d2(:,2:4) = d2(:, 2:4) - d1(:,2:4); %linear acceleration
    %d2(:,2:4) = smooth(d2(:,2:4), 0.95);                    

    annot_count = size(annots, 1);
    for i = 1:annot_count
        s = annots(i,1);
        label = annots(i,2);            
        close all;
        figure('units','normalized','outerposition',[0 0 1 1]);
        %figure

        d = d1;
        d = d(d(:,1) >= s-offset & d(:,1) <= s+offset, :);            
        subplot(2,1,1)       % add first plot in 2 x 1 grid

        ttl = strcat('sub:', num2str(sub), ', annot no:', num2str(i), ', time:', time2str(s), ', label:', num2str(label) );            
        x = d(:,1); % timestamps
        x = x - x(1);
        y = d(:,2:4);        
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
        y = d(:,4);
        %y = fft(y);        
        %y(10:end-9) = 0;
        %y = ifft(y);       
%         y = d(:,2:4); %x, y, z axes val
%         mag = sqrt(sum(y.*y, 2));
%         y = [y, mag];
        plot(x, y);
%        legend('X','Y','Z', 'M');                        
        hold on
        grid on
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
        scatter(offset, 0, 'rd'); % annotated mark point          

        waitforbuttonpress            
    end
                
end



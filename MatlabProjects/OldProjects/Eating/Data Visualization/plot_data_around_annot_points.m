%load('data')
offset = 30;

for sub = 5:34    
    d1 = data(sub).accel;
    rate = length(d1)/(d1(end,1)-d1(1,1))
    
      
    df = d1(:, 2:4);
    df = df(2:end, :) - df(1:end-1, :);
    dfm = sum(abs(df), 2);
    dfm = mysmooth(dfm, 0.9);
    df = [df, dfm];
    %df = round(df, 1);
    annots = data(sub).annots;    
    %annots = annots(annots(:,2)>400 & annots(:,2)<1000, :);                
    %annots = annots(annots(:,2)==601, :);                
    

    %d1(:, 2) = round(d1(:, 2), 0);
    annot_count = size(annots, 1)
    for i = 1:annot_count
        s = annots(i,1);        
        label = annots(i,2);            
        %if label>=400; continue; end
        close all;
        figure('units','normalized','outerposition',[0 0 1 1]);
        %figure

        d = d1(d1(:,1) >= s-offset & d1(:,1) <= s+offset, :);            
        subplot(2,1,1)       % add first plot in 2 x 1 grid

        ttl = strcat('sub:', num2str(sub), ', annot no:', num2str(i), ', time:', num2str(annots(i,1)), ', label:', num2str(label) );            
        x = d(:,1); % timestamps
        x = x - s;
        y = d(:,2:4);        
        plot(x, y);
        legend('X','Y','Z');
        title(ttl);
        hold on
        grid on
        plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
        scatter(0, 0, 'rd'); % annotated mark point        

        d = df(d1(:,1) >= s-offset & d1(:,1) <= s+offset, :);   
        d = [mysmooth(d, 0.5)];
        subplot(2,1,2)       % add second plot in 2 x 1 grid            
        %x = d(:, 1); % timestamps
        %x = x - s;
        y = d;
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
        scatter(0, 0, 'rd'); % annotated mark point          
      

        waitforbuttonpress
        %waitforbuttonpress
    end
                
end



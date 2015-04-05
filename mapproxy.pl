#!/user/home/farrukh/html/cgi-bin/perl
($url) = $ENV{'QUERY_STRING'};
$url =~ tr/+/ /;
$url =~ s/%([a-fA-F0-9][a-fA-F0-9])/pack("C", hex($1))/eg;
$port = 80;
$pos += 3;
$server_name = "arcane.vicinity.com";
$file_name = "/gif?".$url;
$AF_INET =2;
$SOCK_STREAM =1;
$sockaddr = 'S n a4 x8';
($name, $aliases, $proto) = getprotobyname( 'tcp' );
($name, $aliases, $type, $len, $thataddr ) = gethostbyname( $server_name );
$that = pack( $sockaddr, $AF_INET, $port, $thataddr );
if( !socket( S, $AF_INET, $SOCK_STREAM, $proto ) )
{
die $!;
}
if( !connect( S, $that ) )
{
die $!;
}
select( S ); $|=1; select( STDOUT );
$command = "GET ".$file_name;
print S $command."\r\n";

print "Content-type: image/gif\n\n";
while( <S> )
{
print;
}
